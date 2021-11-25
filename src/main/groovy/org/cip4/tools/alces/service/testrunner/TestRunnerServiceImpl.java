package org.cip4.tools.alces.service.testrunner;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Multipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.jmf.JMFMessageBuilder;
import org.cip4.tools.alces.service.testrunner.model.*;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.jdf.JDFPreprocessor;
import org.cip4.tools.alces.preprocessor.jdf.JobIDPreprocessor;
import org.cip4.tools.alces.preprocessor.jdf.NodeInfoPreprocessor;
import org.cip4.tools.alces.preprocessor.jdf.UrlResolvingPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.Preprocessor;
import org.cip4.tools.alces.preprocessor.jmf.SenderIDPreprocessor;
import org.cip4.tools.alces.preprocessor.jmf.URLPreprocessor;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;
import org.cip4.tools.alces.service.testrunner.tests.Test;
import org.cip4.tools.alces.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This is the class in the Alces framework that is responsible for creating test sessions during
 * which JMF messages are sent, received, and tested.
 */
@Service
public class TestRunnerServiceImpl implements TestRunnerService {

    private static Logger log = LoggerFactory.getLogger(TestRunnerServiceImpl.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private List<JmfTest> jmfTests;

    private List<TestSessionsListener> testSessionsListeners;

    private List<TestSession> testSessions;

    /**
     * Default constructor.
     */
    private TestRunnerServiceImpl() {
        this.testSessions = new ArrayList<>();
        testSessionsListeners = new ArrayList<>();
    }

    /**
     * Returns the list of active test sessions.
     * @return The list of active test sessions.
     */
    @Override
    public List<TestSession> getTestSessions() {
        return testSessions;
    }

    @Override
    public void clearTestSessions() {

        // remove all test sessions
        testSessions.clear();

        // notify listeners
        notifyTestSessionsListeners(testSessions);
    }

    @Override
    public void clearTestSession(TestSession testSession) {

        // remove given test session
        testSessions.remove(testSession);

        // notify listeners
        notifyTestSessionsListeners(testSessions);
    }

    @Override
    public void registerTestSuiteListener(TestSessionsListener testSessionsListener) {
        testSessionsListeners.add(testSessionsListener);
    }

    /**
     * Send a JMF Message in the context of a test session.
     */
    private synchronized void sendMessage(TestSession testSession, OutgoingJmfMessage outgoingJmfMessage) {

        testSession.getOutgoingJmfMessages().add(outgoingJmfMessage);

        // run outgoing tests on message and log results
        runTests(testSession.getOutTests(), outgoingJmfMessage);

        // send message
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", outgoingJmfMessage.getContentType());
        HttpEntity<String> request = new HttpEntity<>(outgoingJmfMessage.getBody(), httpHeaders);

        RestTemplate restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(testSession.getTargetUrl(), request, String.class);

        List<String> responseHeaders = responseEntity.getHeaders().get("Content-Type");

        IncomingJmfMessage responseMessage = new IncomingJmfMessage(responseHeaders.get(0), "n. a.", responseEntity.getBody(), false);

        receiveMessage(testSession, responseMessage, outgoingJmfMessage);

    }


    private synchronized OutgoingJmfMessage getOutgoingMessage(TestSession testSession, IncomingJmfMessage message) {
        final JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
        if (jmf == null) {
            return null;
        }
        final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
        final String refId = jmfMsg.getrefID();
        log.debug("Getting outgoing JMF message for incoming JMF message with refID '" + refId + "'...");

        for (OutgoingJmfMessage mOut : testSession.getOutgoingJmfMessages()) {
            JDFJMF jmfOut = JmfUtil.getBodyAsJMF(mOut);
            if (mOut.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
                JDFMessage jmfMsgOut = jmfOut.getMessageElement(null, null, 0);
                if (jmfMsgOut != null && refId.startsWith(jmfMsgOut.getID())) {
                    log.debug("Found outgoing JMF message with refID '" + jmfMsgOut.getID() + "' that matches incoming JMF message with refID '" + refId + "'.");
                    return mOut;
                }
            } else if (mOut.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
                log.debug("Looking for refID '" + refId + "' in outgoing JMF MIME package...");
                AbstractJmfMessage tempMsg = getJMFFromMime(mOut);
                if (tempMsg != null) {
                    String mimeId = JmfUtil.getBodyAsJMF(tempMsg).getMessageElement(null, null, 0).getID();
                    if (refId.startsWith(mimeId)) {
                        log.debug("Found matching refID '" + mimeId + "' in JMF MIME package.");
                    }
                    return mOut;
                }
            }
        }
        log.warn("No outgoing message was found that matches the incoming message with refID '" + refId + "'.");
        return null;
    }

    /**
     * Runs tests on a message and writes the test results to the message object.
     * @param tests List of test to be applied (legacy approach)
     * @param jmfMessage the jmf message to be tested.
     */
    private void runTests(List<Test> tests, AbstractJmfMessage jmfMessage) {

        // filter tets
        List<JmfTest> jmfTests;

        if(jmfMessage instanceof IncomingJmfMessage) {
            jmfTests = this.jmfTests.stream()
                    .filter(jmfTest -> jmfTest.getType() == JmfTest.Type.JMF_IN_TEST || jmfTest.getType() == JmfTest.Type.JMF_BOTH_TEST)
                    .collect(Collectors.toList());

        } else {
            jmfTests = this.jmfTests.stream()
                    .filter(jmfTest -> jmfTest.getType() == JmfTest.Type.JMF_OUT_TEST || jmfTest.getType() == JmfTest.Type.JMF_BOTH_TEST)
                    .collect(Collectors.toList());

        }

        // run tests
        jmfTests.forEach(jmfTest -> {
            TestResult result = jmfTest.runTest(jmfMessage);
            jmfMessage.getTestResults().add(result);
        });

        // legacy tests
        for (Test test : tests) {
            TestResult result = test.runTest(jmfMessage);
            jmfMessage.getTestResults().add(result);
        }
    }

    /**
     * A hack that extracts a JMF message from a Message whose body is a JMF MIME package. A new Message is created using the extracted JMF message as the body.
     *
     * @param message a Message containing a JMF MIME package
     * @return a Message containing the JMF message found in the JMF MIME package
     */
    private static AbstractJmfMessage getJMFFromMime(AbstractJmfMessage message) {
        log.debug("Extracting JMF from JMF MIME package...");
        try {
            // Read message body as input stream
            InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
            // Extract MIME package
            File outputDir = new File(System.getProperty("java.io.tmpdir"));
            String[] fileUrls = org.cip4.tools.alces.util.MimeUtil.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
            // Load first file, JMF is always at first position
            for (int i = 0; i < fileUrls.length; i++) {
                if (fileUrls[i].endsWith(JDFConstants.JMF_EXTENSION)) {
                    String body = IOUtils.toString(new FileInputStream(new File(new URI(fileUrls[i]))));
                    OutgoingJmfMessage tempMsgOut = new OutgoingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, "", body, false);
                    log.debug("Extracted JMF from JMF MIME package: " + tempMsgOut);
                    return tempMsgOut;
                }
            }
        } catch (IOException ioe) {
            log.error("Could not extract JMF from outgoing message's MIME package.", ioe);
        } catch (URISyntaxException use) {
            log.error("Could not extract JMF from outgoing message's MIME package.", use);
        }
        return null;
    }

    private void receiveMessage(TestSession testSession, IncomingJmfMessage incomingJmfMessage, OutgoingJmfMessage outgoingJmfMessage) {

//		if (testSession.getOutgoingMessages()outgoingJmfMessage.size() == 0 && inMessages.size() == 0) {
//			message = inMessage;
//		}

        testSession.getIncomingJmfMessages().add(incomingJmfMessage);

        // Run incoming tests on message
        runTests(testSession.getInTests(), incomingJmfMessage); // Tests must be run before

        // adding InMessage to
        // OutMessage

        if (outgoingJmfMessage != null) {
            outgoingJmfMessage.getIncomingJmfMessages().add(incomingJmfMessage);
        }

        // notifyListeners(incomingJmfMessage, testSession);
    }

    /**
     * Notify all listener about the updated test sessions.
     *
     * @param testSessions The updated list of test sessions.
     */
    private void notifyTestSessionsListeners(List<TestSession> testSessions) {
        this.testSessionsListeners.forEach(testSessionsListener -> testSessionsListener.handleTestSessionsUpdate(
                Collections.unmodifiableList(testSessions)
        ));
    }

    @Override
    public void processIncomingJmfMessage(IncomingJmfMessage inMessage, String jmfEndpointUrl) {

        // get test sesstion for in-message
        TestSession testSession = findTestSession(inMessage);

        // Add the message to the TestSession
        if (testSession != null) {
            receiveMessage(testSession, inMessage);

        } else {
            log.warn("No test session found that matches the message: {}", inMessage);
            log.info("Creating new TestSession for InMessage...");

            // Create a objects using factory

            IncomingJmfMessage incomingJmfMessage = new IncomingJmfMessage(inMessage.getContentType(), inMessage.getHeader(), inMessage.getBody(), true);
            testSession = new TestSession(jmfEndpointUrl, incomingJmfMessage);

            // Add TestSession to suite
            testSessions.add(testSession);

            // Configure tests
            settingsService.configureIncomingTests(testSession);
            settingsService.configureOutgoingTests(testSession);

            // Add message to TestSession
            receiveMessage(testSession, incomingJmfMessage);
        }
    }

    /**
     * Called when a new message is received by this test session.
     */
    private synchronized void receiveMessage(TestSession testSession, IncomingJmfMessage incomingJmfMessage) {
        log.debug("Looking up outgoing message for incoming message.");
        OutgoingJmfMessage outgoingJmfMessage = getOutgoingMessage(testSession, incomingJmfMessage);
        if (outgoingJmfMessage == null) {
            log.debug("No outgoing message could be found for that matches the incoming message.");
        }
        receiveMessage(testSession, incomingJmfMessage, outgoingJmfMessage);
    }

    /**
     * Runs a suite of tests.
     *
     * @param targetUrl   the URL to send the test data to
     * @param testDataDir the directory containing the test data
     * @return the XML file containing the test suite's results
     * @throws Exception
     */
    private void runTests(String targetUrl, String testDataDir) throws Exception {

        // Loads test files
        File[] testFiles = loadTestData(testDataDir);

        log.info("Running tests...");
        int testDelay = Integer.parseInt(settingsService.getProp(SettingsServiceImpl.SEND_DELAY));
        for (int i = 0; i < testFiles.length; i++) {
            log.info("Posting '" + testFiles[i].getAbsolutePath() + "' to " + targetUrl + "...");
            startTestSession(testFiles[i], targetUrl);
            try {
                // Sleep between tests
                Thread.sleep(testDelay);
            } catch (InterruptedException ie) {
            }
        }
        // Wait for asynchronous messages for the entire test duration
        try {
            int sleepTime = Integer.parseInt(settingsService.getProp(SettingsServiceImpl.SESSION_DURATION));
            log.info("Waiting " + sleepTime + " millis for incoming messages...");
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
        }
        log.info("Finished running tests.");
    }

    @Override
    public TestSession startTestSession(String jmfMessage, String targetUrl) {
        return startTestSession(new OutgoingJmfMessage(jmfMessage), null, null, targetUrl);
    }

    @Override
    public TestSession startTestSession(OutgoingJmfMessage message, String targetUrl) {
        return startTestSession(message, null, new PreprocessorContext(), targetUrl);
    }

    /**
     * Starts a new test session using the specified file as the test session's initiating outgoing message.
     * <p>
     * All outgoing messages sent and incoming messages received during a test session are tested by the {@link Test}s
     * configured for this <code>TestRunner</code>.
     * <p>
     * All outgoing messages are preprocessed by the {@link org.cip4.tools.alces.preprocessor.jmf.Preprocessor}s configured for this <code>TestRunner</code>.
     *
     * @param testFile  a file containing the message that starts the test session, for example a JMF message or a MIME package
     * @param targetUrl the URL to send the outgoing message to
     * @return the started test session
     */
    private TestSession startTestSession(File testFile, String targetUrl) {
        return startTestSession(loadMessage(testFile), null, new PreprocessorContext(), targetUrl);
    }

    /**
     * Starts a <code>TestSession</code> based on the specified outgoing message.
     *
     * @param outMessage the outgoing message that starts the test session
     * @param jdfFile    the JDF file submitted by the message; <code>null</code> if no JDF file was submitted
     * @param context    the context used when preprocessing the message and the JDF file
     * @param targetUrl  the URL to send the outgoing message to
     * @return the started test session
     */
    private TestSession startTestSession(OutgoingJmfMessage outMessage, File jdfFile, PreprocessorContext context, String targetUrl) {
        return startTestSession(outMessage, jdfFile, context, targetUrl, false);
    }

    /**
     * Starts a new <code>TestSession</code> by submitting a JDF file.
     *
     * @param outgoingJmfMessage the message used to submit the JDF file
     * @param jdfFile    the JDF file to submit
     * @param context    the preprocessing context
     * @param targetUrl  the URL to submit the JDF file to
     * @param asMime     <code>true</code> to package the JDF file, its content files, and the SubmitQueueEntry JMF message in a MIME package
     * @return
     */
    private TestSession startTestSession(OutgoingJmfMessage outgoingJmfMessage, File jdfFile, PreprocessorContext context, String targetUrl, boolean asMime) {

        if (context != null) {
            // Preprocess message
            context.addAttribute(SenderIDPreprocessor.SENDERID_ATTR, SettingsServiceImpl.getSenderId());
            context.addAttribute(URLPreprocessor.URL_ATTR, settingsService.getServerJmfUrl());
            boolean mjmDetected = outgoingJmfMessage.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE);
            if (mjmDetected) {
                outgoingJmfMessage = preprocessMIME(outgoingJmfMessage, context);
            } else {
                outgoingJmfMessage = preprocessJMF(outgoingJmfMessage, context);
            }
            // Preprocess JDF
            if (jdfFile != null) {
                context.addAttribute(NodeInfoPreprocessor.SUBSCRIPTION_URL_ATTR, settingsService.getServerJmfUrl());
                context.addAttribute(NodeInfoPreprocessor.MESSAGEID_PREFIX_ATTR, JmfUtil.getBodyAsJMF(outgoingJmfMessage).getMessageElement(null, null, 0).getID());
                preprocessJDF(jdfFile, context);
            }
        }

        if (asMime) {
            outgoingJmfMessage = packageAsMime(outgoingJmfMessage, jdfFile);
        }

        // Configure and start test session
        synchronized (testSessions) {
            log.debug("Configuring test session...");
            final TestSession testSession = new TestSession(targetUrl, outgoingJmfMessage);
            testSessions.add(testSession);

            // Configure tests
            settingsService.configureIncomingTests(testSession);
            settingsService.configureOutgoingTests(testSession);

            // Send message
            log.debug("Starting test session and sending message...");
            sendMessage(testSession, outgoingJmfMessage);

            // notify listeners
            notifyTestSessionsListeners(testSessions);

            return testSession;
        }
    }

    /**
     * Finds the TestSession that the messages belongs to.
     *
     * @param message
     * @return
     */
    private synchronized TestSession findTestSession(AbstractJmfMessage message) {
        final JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
        if (jmf == null) {
            log.debug("Incoming message does not contain JMF; TestSession not found.");
            return null;
        }
        final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
        final String refId = jmfMsg.getrefID();
        log.debug("Searching for test session for incoming JMF message with refID '" + refId + "'...");
        for (TestSession testSession : testSessions) {
            for (AbstractJmfMessage mOut : testSession.getOutgoingJmfMessages()) {
                JDFJMF jmfOut = JmfUtil.getBodyAsJMF(mOut);
                if (jmfOut == null) {
                    log.debug("TestSession's outgoing message does not contain JMF; incoming message cannot be matched to outgoing message.");
                    continue;
                }
                JDFMessage jmfMsgOut = jmfOut.getMessageElement(null, null, 0);
                if (refId.startsWith(jmfMsgOut.getID())) {
                    log.debug("Found test session with refID '" + jmfMsgOut.getID() + "' that matches incoming message with refID '" + refId + "'.");
                    return testSession;
                }
            }
        }
        log.warn("No test session was found that matches incoming JMF message with refID '" + refId + "'.");
        return null;
    }

    /**
     * Packages an the JMF message contained in <code>outMessage</code>, the JDF file, and all files referenced by the JDF file in a MIME package that is
     * wrapped in an <code>OutMessage</code>.
     *
     * @param outMessage the JMF message to package together with the JDF file
     * @param jdfFile    the JDF file to package
     * @return an OutMessage which body is the MIME package
     * @todo Refactor! We really need a smarter base implementation of Message that does no hold the entire message in memory. MIME packages can be huge...
     */
    private OutgoingJmfMessage packageAsMime(OutgoingJmfMessage outMessage, File jdfFile) {
        JDFDoc jdfDoc = new JDFParser().parseFile(jdfFile.getAbsolutePath());
        if (jdfFile instanceof PublishedFile) {
            jdfDoc.setOriginalFileName(((PublishedFile) jdfFile).getOriginalFile().getAbsolutePath());
        }
        JDFDoc jmfDoc = new JDFDoc(JmfUtil.getBodyAsJMF(outMessage).getOwnerDocument());
        jmfDoc.setOriginalFileName("message.jmf");
        Multipart multipart = MimeUtil.buildMimePackage(jmfDoc, jdfDoc);
        OutgoingJmfMessage mimeMessage = null;
        try {
            File mjmFile = File.createTempFile("alces", ".mjm");
            mjmFile.deleteOnExit();
            MimeUtil.writeToFile(multipart, mjmFile.getAbsolutePath()); // XXX
            // Check
            // for
            // null
            mimeMessage = loadMessage(mjmFile);
        } catch (IOException e) {
            log.error("Could not write MIME package: " + e.getMessage(), e);
        }
        return mimeMessage;
    }

    /**
     * Loads Alces test data, the JMF messages and JDF instances to be sent.
     *
     * @param testdataPath the path to a test file or directory containing test files to load the test data from
     */
    private File[] loadTestData(String testdataPath) {
        File testDir = new File(testdataPath);
        log.info("Loading test data from '" + testDir.getAbsolutePath() + "'...");
        final File[] testFiles;
        // Checks that the directory exists
        if (testDir.isDirectory()) {
            testFiles = testDir.listFiles(new NotDirFilter());
        } else {
            testFiles = new File[]{new File(testdataPath)};
        }
        log.info("Test data loaded (" + testFiles.length + " files).");
        return testFiles;
    }

    /**
     * Sends a <code>OutMessage</code> to the preconfigured target URL. The message is preprocessed before it is sent.
     * <p>
     * If <code>SenderIDPreprocessor</code> is enabled then <em>JMF/@SenderID</em> will be replaced with the value configured SenderID.
     *
     * @param message the <code>InMessage</code> received in the response
     * @return
     * @throws IOException if an communication exception occurs during the message sending
     */
    private IncomingJmfMessage sendMessage(OutgoingJmfMessage message, String targetUrl) throws IOException {
        PreprocessorContext context = new PreprocessorContext();
        context.addAttribute(SenderIDPreprocessor.SENDERID_ATTR, SettingsServiceImpl.getSenderId());
        boolean mjmDetected = message.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE);
        if (mjmDetected) {
            preprocessMIME(message, context);
        } else {
            preprocessJMF(message, context);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", message.getContentType());
        HttpEntity<String> request = new HttpEntity<>(message.getBody(), httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, request, String.class);

        List<String> responseHeaders = responseEntity.getHeaders().get("Content-Type");

        return new IncomingJmfMessage(responseHeaders.get(0), "n. a.", responseEntity.getBody(), false);
    }

    /**
     * Loads a message from a file.
     *
     * @param file
     * @return
     */
    @Override
    public OutgoingJmfMessage loadMessage(File file) {
        log.debug("Loading message from file '" + file.getAbsolutePath() + "'...");
        String contentType = null;
        String header = null;
        String body = null;
        try {
            if (file.getName().endsWith(JDFConstants.JMF_EXTENSION)) {
                contentType = JDFConstants.JMF_CONTENT_TYPE;
            } else if (file.getName().endsWith(JDFConstants.JDF_EXTENSION)) {
                contentType = JDFConstants.JDF_CONTENT_TYPE;
            } else if (file.getName().endsWith(JDFConstants.JMF_MIME_EXTENSION)) {
                contentType = JDFConstants.MIME_CONTENT_TYPE;
            } else if (file.getName().endsWith(JDFConstants.JDF_MIME_EXTENSION)) {
                contentType = JDFConstants.MIME_CONTENT_TYPE;
            } else if (file.getName().endsWith(JDFConstants.XML_EXTENSION)) {
                contentType = JDFConstants.XML_CONTENT_TYPE;
            } else {
                contentType = "text/plain";
            }
            body = IOUtils.toString(new FileInputStream(file));
        } catch (IOException ioe) {
            log.error("Could not send message because it could not be loaded from file.", ioe);
            return null;
        }
        log.debug("Loaded message from file.");


        OutgoingJmfMessage message = new OutgoingJmfMessage(contentType, header, body, true); // new

        return message;
    }

    /**
     * Starts a new test session by resubmitting a JDF file. Any relative URLs in the JDF file will be resolved against the JDF file and replaced with http
     * URLs.
     *
     * @param jdfFile       the JDF file to submit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf <code>true</code> to preprocess the JDF file before submitting it; <code>false</code> otherwise
     * @return the test session
     */
    private TestSession startTestSessionWithSubmitQueueEntryWithHttpUrls(File jdfFile, String targetUrl, boolean preprocessJdf) {
        return startTestSessionWithSubmitQueueEntry(jdfFile, targetUrl, preprocessJdf, false, getPublishedJDFBaseURL());
    }

    /**
     * Starts a new test session by resubmitting a JDF file. Any relative URLs in the JDF file will be resolved against the JDF file and replaced with absolute
     * file URLs.
     *
     * @param jdfFile       the JDF file to submit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf if <code>true</code> the JDF file is preprocessed before sent
     * @return the test session
     */
    private TestSession startTestSessionWithSubmitQueueEntryWithFileUrls(File jdfFile, String targetUrl, boolean preprocessJdf) {
        return startTestSessionWithSubmitQueueEntry(jdfFile, targetUrl, preprocessJdf, false, jdfFile.getParentFile().toURI().toASCIIString());
    }

    /**
     * Starts a new test session by sending a SubmitQueueEntry JMF message that refers to the specified JDF file. Before submitting the JDF job, the JDF file is
     * published to this TestRunners HTTP server and the resulting http URL is used in the SubmitQueueEntry JMF message.
     *
     * @param jdfFile       the JDF file to submit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf <code>true</code> to preprocess the JDF file before submitting it; <code>false</code> otherwise
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @param baseUrl       If the JDF file references any files using relative URLs then these will be resolved against this base URL. <code>null</code> to not
     *                      resolve any URLs.
     * @return the test session
     */
    private TestSession startTestSessionWithSubmitQueueEntry(File jdfFile, String targetUrl, boolean preprocessJdf, boolean asMime, String baseUrl) {
        jdfFile = publishJDF(jdfFile);
        PreprocessorContext context = new PreprocessorContext();
        context.addAttribute(JDFPreprocessor.PREPROCESSING_ENABLED, Boolean.toString(preprocessJdf));
        context.addAttribute(UrlResolvingPreprocessor.BASEURL_ATTR, baseUrl);
        // String jdfUrl = publishJDF(jdfFile, replaceJobId, baseUrl);
        OutgoingJmfMessage message = JMFMessageBuilder.buildSubmitQueueEntry(resolvePublishedJDF(jdfFile));
        return startTestSession(message, jdfFile, context, targetUrl, asMime);
    }

    /**
     * Starts a new test session by sending a SubmitQueueEntry JMF message that refers to the specified JDF file. Before submitting the JDF job, the JDF file is
     * published to this TestRunners HTTP server and the resulting http URL is used in the SubmitQueueEntry JMF message.
     *
     * @param jdfFile       the JDF file to submit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf <code>true</code> to preprocess the JDF file before submitting it; <code>false</code> otherwise
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    @Override
    public TestSession startTestSessionWithSubmitQueueEntry(File jdfFile, String targetUrl, boolean preprocessJdf, boolean asMime) {
        final String baseUrl;
        final String replaceUrls = settingsService.getProp(SettingsServiceImpl.REPLACE_URLS_IN_JDF);
        if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_WITH_HTTP)) {
            baseUrl = getPublishedJDFBaseURL();
        } else if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_WITH_FILE)) {
            baseUrl = jdfFile.getParentFile().toURI().toASCIIString();
        } else if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_DISABLED)) {
            baseUrl = null;
        } else {
            log.warn("Unknown configuration option for replacing relative URLs in JDF files. " + "Using property value as base URL: " + replaceUrls);
            baseUrl = replaceUrls;
        }
        return startTestSessionWithSubmitQueueEntry(jdfFile, targetUrl, preprocessJdf, asMime, baseUrl);
    }

    /**
     * Starts a new test session by sending a <i>ResubmitQueueEntry</i> JMF message. that refers to the specified JDF file.
     * <p>
     * If <code>asMime</code> is <code>true</code> the JDF file, its content files, and the <i>ResubmitQueueEntry</i> JMF message are bundled in a MIME package.
     * <p>
     * If <code>asMime</code> is <code>false</code>, the JDF job, the JDF file is published to this TestRunners HTTP server and the resulting http URL is used
     * in the <i>ResubmitQueueEntry</i> JMF message. Any relative URLs in the JDF file will be resolved against <code>baseUrl</code> and replaced with an
     *
     * @param jdfFile       the JDF file to submit
     * @param queueEntryId  the queue entry ID of the job to resubmit
     * @param jobId         the /JDF/@JobID of the job to resubmit
     * @param targetUrl     the URL to submit it to
     * @param baseUrl       If the JDF file references any files using relative URLs then these will be resolved against this base URL. <code>null</code> to not
     *                      resolve and replace any URLs.
     * @param preprocessJdf
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    private TestSession startTestSessionWithResubmitQueueEntry(File jdfFile, String queueEntryId, String jobId, String targetUrl, String baseUrl, boolean preprocessJdf, boolean asMime) {
        jdfFile = publishJDF(jdfFile);
        PreprocessorContext context = new PreprocessorContext();
        context.addAttribute(JDFPreprocessor.PREPROCESSING_ENABLED, Boolean.toString(preprocessJdf));
        context.addAttribute(JobIDPreprocessor.JOBID_ATTR, jobId);
        context.addAttribute(UrlResolvingPreprocessor.BASEURL_ATTR, baseUrl);
        OutgoingJmfMessage message = JMFMessageBuilder.buildResubmitQueueEntry(resolvePublishedJDF(jdfFile), queueEntryId);
        return startTestSession(message, jdfFile, context, targetUrl, asMime);
    }

    /**
     * Starts a new test session by sending a <i>ResubmitQueueEntry</i> JMF message. that refers to the specified JDF file.
     * <p>
     * If <code>asMime</code> is <code>true</code> the JDF file, its content files, and the <i>ResubmitQueueEntry</i> JMF message are bundled in a MIME package.
     * <p>
     * If <code>asMime</code> is <code>false</code>, the JDF job, the JDF file is published to this TestRunners HTTP server and the resulting http URL is used
     * in the <i>ResubmitQueueEntry</i> JMF message.
     *
     * @param jdfFile       the JDF file to submit
     * @param queueEntryId  the queue entry ID of the job to resubmit
     * @param jobId         the /JDF/@JobID of the job to resubmit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    @Override
    public TestSession startTestSessionWithResubmitQueueEntry(File jdfFile, String queueEntryId, String jobId, String targetUrl, boolean preprocessJdf, boolean asMime) {
        final String baseUrl;
        final String replaceUrls = settingsService.getProp(SettingsServiceImpl.REPLACE_URLS_IN_JDF);
        if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_WITH_HTTP)) {
            baseUrl = getPublishedJDFBaseURL();
        } else if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_WITH_FILE)) {
            baseUrl = jdfFile.getParentFile().toURI().toASCIIString();
        } else if (replaceUrls.equals(SettingsServiceImpl.REPLACE_URLS_IN_JDF_DISABLED)) {
            baseUrl = null;
        } else {
            log.warn("Unknown configuration option for replacing relative URLs in JDF files. " + "Using property value as base URL: " + replaceUrls);
            baseUrl = replaceUrls;
        }
        return startTestSessionWithResubmitQueueEntry(jdfFile, queueEntryId, jobId, targetUrl, baseUrl, preprocessJdf, asMime);
    }

    /**
     * Publishes a JDF file to Alces's HTTP server's public directory.
     *
     * @param jdfFile the JDF file to publish
     * @return the published JDF file
     */
    private File publishJDF(File jdfFile) {
        log.debug("Publishing JDF '" + jdfFile.getAbsolutePath() + "' to web server ...");
        if (!jdfFile.exists()) {
            throw new IllegalArgumentException("The JDF file '" + jdfFile.getAbsolutePath() + "' cannot be published because the file does not exist.");
        }
        String publicDirPath = settingsService.getProp(SettingsServiceImpl.RESOURCE_BASE);
        // Create JDF dir
        File publicJdfDir = new File(publicDirPath, "jdf");
        publicJdfDir.mkdir();
        // Create public JDF filename
        String jdfFilename = RandomStringUtils.randomAlphanumeric(16) + ".jdf";
        File publicJdfFile = new PublishedFile(publicJdfDir, jdfFilename, jdfFile);
        try {
            FileUtils.copyFile(jdfFile, publicJdfFile);
        } catch (IOException e) {
            log.error("The JDF file '" + jdfFile + "' could not be published to '" + publicJdfFile + "'.", e);
        }
        return publicJdfFile;
    }

    /**
     * Returns the HTTP URL of the directory where JDF files are published so that they can be downloaded.
     *
     * @return the HTTP URL, ending with a slash character, where JDF files are published
     */
    private String getPublishedJDFBaseURL() {
        String publicJdfUrl = null;
        String host = settingsService.getProp(SettingsServiceImpl.HOST);
        int port = Integer.parseInt(settingsService.getProp(SettingsServiceImpl.PORT));
        publicJdfUrl = "http://" + host + ":" + port + "/jdf/";
        return publicJdfUrl;
    }

    /**
     * Resolves a published JDF file to a HTTP URL.
     *
     * @param jdfFile
     * @return a HTTP URL
     */
    private String resolvePublishedJDF(File jdfFile) {
        return getPublishedJDFBaseURL() + jdfFile.getName();
    }

    /**
     * Preprocesses a MIME package. This methods implementation currently has the following limitations:
     * <ul>
     * <li>Only the JMF is preprocessed, not the JDF</li>
     * <li>Content files in the original MIME package are not included in the new MIME package returned</li>
     * </ul>
     *
     * @param message
     * @param context
     * @return
     * @author Alex Khilov
     * @since 0.9.9.3
     */
    private OutgoingJmfMessage preprocessMIME(OutgoingJmfMessage message, PreprocessorContext context) {
        String mjmEnableStr = settingsService.getProp(SettingsServiceImpl.MJM_MIME_FILE_PARSE);
        boolean mjmEnabled = Boolean.parseBoolean(mjmEnableStr);
        // 3 steps are here: separate MIME message, preprocess JMF part, glue it back.
        if (mjmEnabled) {
            // take JMF part from MIME
            OutgoingJmfMessage jmfMessage = (OutgoingJmfMessage) getJMFFromMime(message);
            // preprocess JMF part
            jmfMessage = preprocessJMF(jmfMessage, context);
            // glue it back
            JDFDoc jdfDoc = new JDFParser().parseFile(getJDFFileFromMime(message));
            JDFDoc jmfDoc = new JDFDoc(JmfUtil.getBodyAsJMF(jmfMessage).getOwnerDocument());
            Multipart multipart = MimeUtil.buildMimePackage(jmfDoc, jdfDoc);
            try {
                File mjmFile = File.createTempFile("alces-PACKAGE-PREPARING-", ".mjm");
                mjmFile.deleteOnExit();
                MimeUtil.writeToFile(multipart, mjmFile.getAbsolutePath());

                message.setBody(IOUtils.toString(new FileInputStream(mjmFile)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return message;
    }

    /**
     * Applies all preprocessors in the List to the Message. Preprocessors are applied in the order they occur in the list. If a preprocessor fails it does so
     * quietly and preprocessing continues.
     *
     * @param message
     */
    private OutgoingJmfMessage preprocessJMF(OutgoingJmfMessage message, PreprocessorContext context) {
        Preprocessor[] preprocessors = settingsService.getJMFPreprocessors();
        log.debug("Preprocessing message with " + preprocessors.length + " preprocessors...");
        for (int i = 0; i < preprocessors.length; i++) {
            try {
                preprocessors[i].preprocess(message, context);
            } catch (Exception e) {
                log.warn("Preprocessing message failed.", e);
            }
        }
        return message;
    }

    /**
     * Preprocesses the JDF file with the currently configured preprocessors. The file is read from disk, preprocessed, and then written back to the same file.
     * If a preprocessor fails it does so quietly and preprocessing continues. If the file could not be parsed a warning is logged and <code>null</code> is
     * returned.
     *
     * @param jdfFile the JDF file to preprocess
     * @param context the preprocessing context
     * @return the preprocessed JDF node that was written back to the file; <code>null</code> if the JDF file could not be parsed
     */
    private JDFNode preprocessJDF(File jdfFile, PreprocessorContext context) {

        JDFDoc jdfDoc = new JDFParser().parseFile(jdfFile.getAbsolutePath());
        if (jdfDoc == null) {
            log.warn("Could not parse JDF file '" + jdfFile.getAbsolutePath() + "' for preprocessing.");
            return null;
        }
        JDFNode jdf = preprocessJDF(jdfDoc.getJDFRoot(), context);
        if (!jdf.getOwnerDocument_KElement().write2File(jdfFile.getAbsolutePath(), 2, true)) {
            log.warn("Could not write preprocessed JDF back to file '" + jdfFile.getAbsolutePath() + "'.");
            return null;
        }
        return jdf;
    }

    /**
     * Preprocesses the JDF node with the currently configured preprocessors. If a preprocessor fails it does so quietly and preprocessing continues.
     *
     * @param jdf     the JDF node to preprocess
     * @param context the preprocessing context
     */
    private JDFNode preprocessJDF(JDFNode jdf, PreprocessorContext context) {
        if (context.getAttribute(JDFPreprocessor.PREPROCESSING_ENABLED).equals("false")) {
            return jdf;
        }
        JDFPreprocessor[] preprocessors = settingsService.getJDFPreprocessors();
        log.debug("Preprocessing JDF with " + preprocessors.length + " preprocessors...");
        for (int i = 0; i < preprocessors.length; i++) {
            try {
                preprocessors[i].preprocess(jdf, context);
            } catch (Exception e) {
                log.warn("Preprocessing message failed.", e);
            }
        }
        return jdf;
    }

    /**
     * A wrapper for a published file.
     *
     * @author Claes Buckwalter
     */
    private class PublishedFile extends File {
        private final File originalFile;

        /**
         * @param parent       the published file's parent path
         * @param child        the published file's name
         * @param originalFile the original file
         */
        PublishedFile(String parent, String child, File originalFile) {
            this(new File(parent), child, originalFile);
        }

        PublishedFile(File parent, String child, File originalFile) {
            super(parent, child);
            this.originalFile = originalFile;
        }

        private File getOriginalFile() {
            return originalFile;
        }
    }

    private static String getJDFFileFromMime(AbstractJmfMessage message) {
        log.warn("Extracting JDF from JMF MIME package...");
        try {
            // Read message body as input stream
            InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
            // Extract MIME package
            File outputDir = new File(System.getProperty("java.io.tmpdir"));
            String[] fileUrls = org.cip4.tools.alces.util.MimeUtil.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
            // Load first file, JMF is always at first position
            for (int i = 0; i < fileUrls.length; i++) {
                if (fileUrls[i].endsWith(JDFConstants.JDF_EXTENSION)) {
                    String body = IOUtils.toString(new FileInputStream(new File(new URI(fileUrls[i]))));
                    OutgoingJmfMessage tempMsgOut = new OutgoingJmfMessage(JDFConstants.JDF_CONTENT_TYPE, "", body, false);
                    log.debug("Extracted JDF from JMF MIME package: " + tempMsgOut);
                    // return tempMsgOut;
                    return fileUrls[i];
                }
            }
        } catch (IOException ioe) {
            log.error("Could not extract JDF from outgoing message's MIME package.", ioe);
        } catch (URISyntaxException use) {
            log.error("Could not extract JDF from outgoing message's MIME package.", use);
        }
        return null;
    }
}
