package org.cip4.tools.alces.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.test.tests.Test;
import org.cip4.tools.alces.util.ApplicationContextUtil;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.cip4.tools.alces.util.MimeUtil;
import org.jdom.Attribute;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Claes Buckwalter
 */
public class TestSession {

	private static Logger log = LoggerFactory.getLogger(TestSession.class);

	private String targetUrl;

	private final List<OutgoingJmfMessage> outMessages;

	private final List<IncomingJmfMessage> inMessages;

	private final List<Test> outTests;

	private final List<Test> inTests;

	private final List<TestResult> testResults;

	private final List<TestSessionListener> testSessionListeners;

	private AbstractJmfMessage message = null;

	/**
	 * Creates a new test session that sends messages to the specified URL. A test session lasts
	 * for a max duration of time. After the specified duration of time has elapsed the test session
	 * is ended and any further messages received will be ignored.
	 * 
	 * @param targetUrl the URL to send messages to during this session
	 */
	public TestSession(String targetUrl) {
		setTargetUrl(targetUrl);
		outMessages = new ArrayList<>();
		inMessages = new ArrayList<>();
		outTests = new ArrayList<>();
		inTests = new ArrayList<>();
		testResults = new ArrayList<>(); // TODO Sort test results based on time?
		testSessionListeners = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "TestSession[ targetUrl=" + targetUrl + " ]";
	}

	/**
	 * Called when a new message is sent by this test session.
	 * 
	 * @param message
	 */
	public synchronized void sendMessage(OutgoingJmfMessage message) {

		// log message
		if (outMessages.size() == 0 && inMessages.size() == 0) {
			this.message = message;
		}
		outMessages.add(message);

		// run outgoing tests on message and log results
		runTests(outTests, message);

		// send message
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", message.getContentType());
		HttpEntity<String> request = new HttpEntity<>(message.getBody(), httpHeaders);

		RestTemplate restTemplate = ApplicationContextUtil.getBean(RestTemplate.class);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(targetUrl, request, String.class);

		List<String> responseHeaders = responseEntity.getHeaders().get("Content-Type");

		IncomingJmfMessage responseMessage = new IncomingJmfMessage(responseHeaders.get(0), "n. a.", responseEntity.getBody(), false);

		receiveMessage(responseMessage, message);

	}

	/**
	 * Called when a new message is received by this test session.
	 * 
	 * @param message the received message
	 */
	public synchronized void receiveMessage(IncomingJmfMessage message) {
		log.debug("Looking up outgoing message for incoming message.");
		OutgoingJmfMessage outMsg = getOutgoingMessage(message);
		if (outMsg == null) {
			log.debug("No outgoing message could be found for that matches the incoming message.");
		}
		receiveMessage(message, outMsg);
	}

	/**
	 * 
	 * @param inMessage
	 * @param outMessage
	 */
	public void receiveMessage(IncomingJmfMessage inMessage, OutgoingJmfMessage outMessage) {
		if (log.isDebugEnabled()) {
			log.debug("Received message: " + inMessage);
		}
		if (outMessages.size() == 0 && inMessages.size() == 0) {
			message = inMessage;
		}
		inMessages.add(inMessage);
		// Run incoming tests on message
		runTests(inTests, inMessage); // Tests must be run before
		// adding InMessage to
		// OutMessage

		if (outMessage != null) {
			outMessage.getIncomingJmfMessages().add(inMessage);
		}

		notifyListeners(inMessage, this);
	}

	/**
	 * Adds a test to perform on outgoing messages.
	 * 
	 * @param test
	 */
	public void addOutgoingTest(Test test) {
		outTests.add(test);
	}

	/**
	 * Adds a test to perform on incoming messages.
	 * 
	 * @param test
	 */
	public void addIncomingTest(Test test) {
		inTests.add(test);
	}

	/**
	 * Adds a result generated by an outgoing or incoming test.
	 * 
	 * @param testResult
	 */
	public void addTestResult(TestResult testResult) {
		testResults.add(testResult);
	}

	/**
	 * Runs tests on a message and logs the test results
	 * 
	 * @param tests
	 * @param message
	 */
	private void runTests(List<Test> tests, AbstractJmfMessage message) {
		log.debug("Running tests on message...");
		for (Test test : tests) {
			log.debug("Running test: " + test.getClass().getName());
			TestResult result = test.runTest(message); // XXX Links
			// TestResult->Message
			message.getTestResults().add(result); // XXX Links Message->TestResult
			addTestResult(result); // XXX Links TestSession->TestResult
		}
	}

	/**
	 * Gets a <code>List</code> of <code>InMessage</code> s that have been received during this <code>TestSession</code>.
	 */
	public List<IncomingJmfMessage> getIncomingMessages() {
		return inMessages;
	}

	/**
	 * Returns a <code>List</code> of <code>OutMessage</code> s that have been sent during this <code>TestSession</code>.
	 */
	public List<OutgoingJmfMessage> getOutgoingMessages() {
		return outMessages;
	}

	public List<TestResult> getTestResults() {
		return testResults;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.test.TestSession#getOutgoingMessage(org.cip4.tools.alces.message.InMessage)
	 */
	public synchronized OutgoingJmfMessage getOutgoingMessage(IncomingJmfMessage message) {
		final JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
		if (jmf == null) {
			return null;
		}
		final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
		final String refId = jmfMsg.getrefID();
		log.debug("Getting outgoing JMF message for incoming JMF message with refID '" + refId + "'...");

		for (OutgoingJmfMessage mOut : getOutgoingMessages()) {
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

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.test.TestSession#getIncomingMessage(org.cip4.tools.alces.message.OutMessage)
	 */
	/**
	 * Finds the incoming message that the specified outgoing message is a response to.
	 * 
	 * @param message the outgoing message
	 * @return the incoming message that the outgoing message is a response to
	 */
	public IncomingJmfMessage getIncomingMessage(OutgoingJmfMessage message) {
		String refID = null;
		try {
			// Configure JDF namespace
			Namespace jdfNamespace = Namespace.getNamespace("jdf", "http://www.CIP4.org/JDFSchema_1_1");

			// Configure XPath for refID
			// XPath refidXPath = XPath.newInstance("jdf:JMF/child::node()/@refID");
			XPath refidXPath = XPath.newInstance("jdf:JMF/child::node()/@ID"); // bug fixed: no @refID in OutMessage
			refidXPath.addNamespace(jdfNamespace);

			// Execute XPath query for refID
			Attribute refIDAttr = (Attribute) refidXPath.selectSingleNode(JmfUtil.getBodyAsJDOM(message));
			refID = refIDAttr.getValue();
			log.info("Found: @refID / @ID = " + refID);

			// Configure XPath for ID
			// XPath idXPath = XPath.newInstance("jdf:JMF/child::node()[@ID='" + refID + "']");
			XPath idXPath = XPath.newInstance("jdf:JMF/child::node()[@refID='" + refID + "']"); // bug fixed: no @ID in InMessage
			idXPath.addNamespace(jdfNamespace);

			synchronized (inMessages) {
				// Go through all messages sent during a session
				for (IncomingJmfMessage msgIn : inMessages) {
					// Execute XPath for ID
					if (idXPath.selectSingleNode(JmfUtil.getBodyAsJDOM(msgIn)) != null) {
						log.debug("Found ID matching refID: " + refID);
						return msgIn;
					}
				}
			}
		} catch (Exception e) {
			log.error("An error occurred while getting InMessage.", e);
		}
		log.debug("No incoming message was found that matches the outgoing message with refID: " + refID);
		return null;
	}

	public AbstractJmfMessage getInitiatingMessage() {
		return message;
	}

	/**
	 * A hack that extracts a JMF message from a Message whose body is a JMF MIME package. A new Message is created using the extracted JMF message as the body.
	 * 
	 * @param message a Message containing a JMF MIME package
	 * @return a Message containing the JMF message found in the JMF MIME package
	 */
	public static AbstractJmfMessage getJMFFromMime(AbstractJmfMessage message) {
		log.debug("Extracting JMF from JMF MIME package...");
		try {
			// Read message body as input stream
			InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
			// Extract MIME package
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			String[] fileUrls = MimeUtil.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
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

	public static String getJDFFileFromMime(AbstractJmfMessage message) {
		log.warn("Extracting JDF from JMF MIME package...");
		try {
			// Read message body as input stream
			InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
			// Extract MIME package
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			String[] fileUrls = MimeUtil.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
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

	public void addListener(TestSessionListener listener) {
		testSessionListeners.add(listener);
	}

	public void removeListener(TestSessionListener listener) {
		testSessionListeners.remove(listener);
	}

	/**
	 * Synchronously notifies listeners that a new message has been received.
	 * 
	 * @param message the received message
	 * @param testSession the message's test session
	 */
	protected void notifyListeners(IncomingJmfMessage message, TestSession testSession) {
		if (testSessionListeners == null || testSessionListeners.size() == 0) {
			return;
		}
		TestSessionListener[] listeners = testSessionListeners.toArray(new TestSessionListener[testSessionListeners.size()]);

		for (int i = 0; i < listeners.length; i++) {
			listeners[i].messageReceived(message, testSession);
		}
	}

	public boolean hasPassedAllTests() {
		boolean passed;

		for (IncomingJmfMessage message : getIncomingMessages()) {
			passed = message.hasPassedAllTests();
			if (!passed) {
				return false;
			}
		}

		for (OutgoingJmfMessage outMessage : getOutgoingMessages()) {
			passed = outMessage.hasPassedAllTests();
			if (!passed) {
				return false;
			}
		}

		return true;
	}
}