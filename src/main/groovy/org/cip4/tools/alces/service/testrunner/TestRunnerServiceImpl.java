package org.cip4.tools.alces.service.testrunner;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.service.testrunner.model.*;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;
import org.cip4.tools.alces.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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

    private final List<TestSession> testSessions;

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
        notifyTestSessionsListeners();
    }

    @Override
    public void clearTestSession(TestSession testSession) {

        // remove given test session
        testSessions.remove(testSession);

        // notify listeners
        notifyTestSessionsListeners();
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
        runTests(outgoingJmfMessage);
        notifyTestSessionsListeners();

        // send message
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", outgoingJmfMessage.getContentType());
        HttpEntity<String> request = new HttpEntity<>(outgoingJmfMessage.getBody(), httpHeaders);

        RestTemplate restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(testSession.getTargetUrl(), request, String.class);

        List<String> responseHeaders = responseEntity.getHeaders().get("Content-Type");

        IncomingJmfMessage responseMessage = new IncomingJmfMessage(responseHeaders.get(0), responseEntity.getBody());

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
            if (mOut.getContentType().startsWith(JDFConstants.MIME_JMF)) {
                JDFMessage jmfMsgOut = jmfOut.getMessageElement(null, null, 0);
                if (jmfMsgOut != null && refId.startsWith(jmfMsgOut.getID())) {
                    log.debug("Found outgoing JMF message with refID '" + jmfMsgOut.getID() + "' that matches incoming JMF message with refID '" + refId + "'.");
                    return mOut;
                }
            } else if (mOut.getContentType().startsWith(MediaType.MULTIPART_RELATED_VALUE)) {
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
     * @param jmfMessage the jmf message to be tested.
     */
    private void runTests(AbstractJmfMessage jmfMessage) {

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
                if (fileUrls[i].endsWith(".jmf")) {
                    String body = IOUtils.toString(new FileInputStream(new File(new URI(fileUrls[i]))));
                    OutgoingJmfMessage tempMsgOut = new OutgoingJmfMessage(JDFConstants.MIME_JMF, body);
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

        testSession.getIncomingJmfMessages().add(incomingJmfMessage);

        // Run incoming tests on message
        runTests(incomingJmfMessage); // Tests must be run before


        if (outgoingJmfMessage != null) {
            outgoingJmfMessage.getIncomingJmfMessages().add(incomingJmfMessage);
        }
    }

    /**
     * Notify all listener about the updated test sessions.
     */
    private void notifyTestSessionsListeners() {
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

            IncomingJmfMessage incomingJmfMessage = new IncomingJmfMessage(inMessage.getContentType(), inMessage.getBody());
            testSession = new TestSession(jmfEndpointUrl, incomingJmfMessage);

            // Add TestSession to suite
            testSessions.add(testSession);

            // Add message to TestSession
            receiveMessage(testSession, incomingJmfMessage);
        }

        notifyTestSessionsListeners();
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

    @Override
    @Async
    public Future<TestSession> startTestSession(String jmfMessage, String targetUrl) {

        // create an outgoing message object from jmf string
        OutgoingJmfMessage outgoingJmfMessage = new OutgoingJmfMessage(jmfMessage);

        TestSession testSession = new TestSession(targetUrl, outgoingJmfMessage);
        testSessions.add(testSession);

        // notify listeners
        notifyTestSessionsListeners();

        // send message
        sendMessage(testSession, outgoingJmfMessage);

        // notify listeners
        notifyTestSessionsListeners();

        // return test session
        return new AsyncResult<>(testSession);
    }


    /**
     * Finds the TestSession that the messages belongs to.
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
}
