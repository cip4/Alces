package org.cip4.tools.alces.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.message.util.mime.MimePackageException;
import org.cip4.tools.alces.message.util.mime.MimeReader;
import org.cip4.tools.alces.test.tests.Test;
import org.cip4.tools.alces.transport.HttpDispatcher;
import org.cip4.tools.alces.util.JDFConstants;
import org.jdom.Attribute;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Claes Buckwalter
 */
public class TestSessionImpl implements TestSession {

	private static Logger log = LoggerFactory.getLogger(TestSessionImpl.class);

	private String _targetUrl;

	private final List<OutMessage> _outgoingMessages;

	private final List<InMessage> _incomingMessages;

	private final List<Test> _outgoingTests;

	private final List<Test> _incomingTests;

	private final List<TestResult> _testResults;

	private final List<TestSessionListener> _listeners;

	private Message _initMessage = null;

	/**
	 * Creates a new test session that sends messages to the specified URL. A test session lasts for a max duration of time. After the specified duration of
	 * time has elapsed the test session is ended and any further messages received will be ignored.
	 * 
	 * @param targetUrl the URL to send messages to during this session
	 */
	public TestSessionImpl(String targetUrl) {
		setTargetUrl(targetUrl);
		_outgoingMessages = new Vector<OutMessage>();
		_incomingMessages = new Vector<InMessage>();
		_outgoingTests = new Vector<Test>();
		_incomingTests = new Vector<Test>();
		_testResults = new Vector<TestResult>(); // TODO Sort test results based on time?
		_listeners = new Vector<TestSessionListener>();
	}

	@Override
	public String toString() {
		return "TestSession[ targetUrl=" + _targetUrl + " ]";
	}

	/**
	 * Called when a new message is sent by this test session.
	 * 
	 * @param message
	 */
	public synchronized void sendMessage(OutMessage message, HttpDispatcher dispatcher) {
		// Log message
		if (_outgoingMessages.size() == 0 && _incomingMessages.size() == 0) {
			_initMessage = message;
		}
		_outgoingMessages.add(message);

		// Run outgoing tests on message and log results
		runTests(_outgoingTests, message);
		// Send message
		InMessage responseMessage;
		try {
			responseMessage = dispatcher.dispatch(message, _targetUrl);
			receiveMessage(responseMessage, message);
		} catch (IOException e) {
			log.error("Could not send message to '" + _targetUrl + "': " + e, e);
		}
	}

	/**
	 * Called when a new message is received by this test session.
	 * 
	 * @param message the received message
	 */
	public synchronized void receiveMessage(InMessage message) {
		log.debug("Looking up outgoing message for incoming message.");
		OutMessage outMsg = getOutgoingMessage(message);
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
	public void receiveMessage(InMessage inMessage, OutMessage outMessage) {
		if (log.isDebugEnabled()) {
			log.debug("Received message: " + inMessage);
		}
		if (_outgoingMessages.size() == 0 && _incomingMessages.size() == 0) {
			_initMessage = inMessage;
		}
		_incomingMessages.add(inMessage);
		// Run incoming tests on message
		runTests(_incomingTests, inMessage); // Tests must be run before
		// adding InMessage to
		// OutMessage

		if (outMessage != null) {
			outMessage.addInMessage(inMessage);
		}
		notifyListeners(inMessage, this);
	}

	/**
	 * Adds a test to perform on outgoing messages.
	 * 
	 * @param test
	 */
	public void addOutgoingTest(Test test) {
		_outgoingTests.add(test);
	}

	/**
	 * Adds a test to perform on incoming messages.
	 * 
	 * @param test
	 */
	public void addIncomingTest(Test test) {
		_incomingTests.add(test);
	}

	/**
	 * Adds a result generated by an outgoing or incoming test.
	 * 
	 * @param testResult
	 */
	public void addTestResult(TestResult testResult) {
		_testResults.add(testResult);
	}

	/**
	 * Runs tests on a message and logs the test results
	 * 
	 * @param tests
	 * @param message
	 */
	private void runTests(List<Test> tests, Message message) {
		log.debug("Running tests on message...");
		for (Test test : tests) {
			log.debug("Running test: " + test.getClass().getName());
			TestResult result = test.runTest(message); // XXX Links
			// TestResult->Message
			message.addTestResult(result); // XXX Links Message->TestResult
			addTestResult(result); // XXX Links TestSession->TestResult
		}
	}

	/**
	 * Gets a <code>List</code> of <code>InMessage</code> s that have been received during this <code>TestSession</code>.
	 */
	public List<InMessage> getIncomingMessages() {
		return _incomingMessages;
	}

	/**
	 * Returns a <code>List</code> of <code>OutMessage</code> s that have been sent during this <code>TestSession</code>.
	 */
	public List<OutMessage> getOutgoingMessages() {
		return _outgoingMessages;
	}

	public List<TestResult> getTestResults() {
		return _testResults;
	}

	public void setTargetUrl(String targetUrl) {
		_targetUrl = targetUrl;
	}

	public String getTargetUrl() {
		return _targetUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.test.TestSession#getOutgoingMessage(org.cip4.tools.alces.message.InMessage)
	 */
	public synchronized OutMessage getOutgoingMessage(InMessage message) {
		final JDFJMF jmf = message.getBodyAsJMF();
		if (jmf == null) {
			return null;
		}
		final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
		final String refId = jmfMsg.getrefID();
		log.debug("Getting outgoing JMF message for incoming JMF message with refID '" + refId + "'...");

		for (OutMessage mOut : getOutgoingMessages()) {
			JDFJMF jmfOut = mOut.getBodyAsJMF();
			if (mOut.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
				JDFMessage jmfMsgOut = jmfOut.getMessageElement(null, null, 0);
				if (jmfMsgOut != null && refId.startsWith(jmfMsgOut.getID())) {
					log.debug("Found outgoing JMF message with refID '" + jmfMsgOut.getID() + "' that matches incoming JMF message with refID '" + refId + "'.");
					return mOut;
				}
			} else if (mOut.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
				log.debug("Looking for refID '" + refId + "' in outgoing JMF MIME package...");
				Message tempMsg = getJMFFromMime(mOut);
				if (tempMsg != null) {
					String mimeId = tempMsg.getBodyAsJMF().getMessageElement(null, null, 0).getID();
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
	public InMessage getIncomingMessage(OutMessage message) {
		String refID = null;
		try {
			// Configure JDF namespace
			Namespace jdfNamespace = Namespace.getNamespace("jdf", "http://www.CIP4.org/JDFSchema_1_1");

			// Configure XPath for refID
			// XPath refidXPath = XPath.newInstance("jdf:JMF/child::node()/@refID");
			XPath refidXPath = XPath.newInstance("jdf:JMF/child::node()/@ID"); // bug fixed: no @refID in OutMessage
			refidXPath.addNamespace(jdfNamespace);

			// Execute XPath query for refID
			Attribute refIDAttr = (Attribute) refidXPath.selectSingleNode(message.getBodyAsJDOM());
			refID = refIDAttr.getValue();
			log.info("Found: @refID / @ID = " + refID);

			// Configure XPath for ID
			// XPath idXPath = XPath.newInstance("jdf:JMF/child::node()[@ID='" + refID + "']");
			XPath idXPath = XPath.newInstance("jdf:JMF/child::node()[@refID='" + refID + "']"); // bug fixed: no @ID in InMessage
			idXPath.addNamespace(jdfNamespace);

			synchronized (_incomingMessages) {
				// Go through all messages sent during a session
				for (InMessage msgIn : _incomingMessages) {
					// Execute XPath for ID
					if (idXPath.selectSingleNode(msgIn.getBodyAsJDOM()) != null) {
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

	public Message getInitiatingMessage() {
		return _initMessage;
	}

	/**
	 * A hack that extracts a JMF message from a Message whose body is a JMF MIME package. A new Message is created using the extracted JMF message as the body.
	 * 
	 * @param message a Message containing a JMF MIME package
	 * @return a Message containing the JMF message found in the JMF MIME package
	 */
	public static Message getJMFFromMime(Message message) {
		log.debug("Extracting JMF from JMF MIME package...");
		try {
			// Read message body as input stream
			InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
			// Extract MIME package
			MimeReader mimeReader = new MimeReader();
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			String[] fileUrls = mimeReader.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
			// Load first file, JMF is always at first position
			for (int i = 0; i < fileUrls.length; i++) {
				if (fileUrls[i].endsWith(JDFConstants.JMF_EXTENSION)) {
					String body = IOUtils.toString(new FileInputStream(new File(new URI(fileUrls[i]))));
					OutMessage tempMsgOut = new OutMessageImpl(JDFConstants.JMF_CONTENT_TYPE, "", body, false);
					log.debug("Extracted JMF from JMF MIME package: " + tempMsgOut);
					return tempMsgOut;
				}
			}
		} catch (IOException ioe) {
			log.error("Could not extract JMF from outgoing message's MIME package.", ioe);
		} catch (MimePackageException mpe) {
			log.error("Could not extract JMF from outgoing message's MIME package.", mpe);
		} catch (URISyntaxException use) {
			log.error("Could not extract JMF from outgoing message's MIME package.", use);
		}
		return null;
	}

	public static String getJDFFileFromMime(Message message) {
		log.warn("Extracting JDF from JMF MIME package...");
		try {
			// Read message body as input stream
			InputStream mimeStream = new ByteArrayInputStream(message.getBody().getBytes());
			// Extract MIME package
			MimeReader mimeReader = new MimeReader();
			File outputDir = new File(System.getProperty("java.io.tmpdir"));
			String[] fileUrls = mimeReader.extractMimePackage(mimeStream, outputDir.toURI().toURL().toExternalForm());
			// Load first file, JMF is always at first position
			for (int i = 0; i < fileUrls.length; i++) {
				if (fileUrls[i].endsWith(JDFConstants.JDF_EXTENSION)) {
					String body = IOUtils.toString(new FileInputStream(new File(new URI(fileUrls[i]))));
					OutMessage tempMsgOut = new OutMessageImpl(JDFConstants.JDF_CONTENT_TYPE, "", body, false);
					log.debug("Extracted JDF from JMF MIME package: " + tempMsgOut);
					// return tempMsgOut;
					return fileUrls[i];
				}
			}
		} catch (IOException ioe) {
			log.error("Could not extract JDF from outgoing message's MIME package.", ioe);
		} catch (MimePackageException mpe) {
			log.error("Could not extract JDF from outgoing message's MIME package.", mpe);
		} catch (URISyntaxException use) {
			log.error("Could not extract JDF from outgoing message's MIME package.", use);
		}
		return null;
	}

	public void addListener(TestSessionListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(TestSessionListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Synchronously notifies listeners that a new message has been received.
	 * 
	 * @param message the received message
	 * @param testSession the message's test session
	 */
	protected void notifyListeners(InMessage message, TestSession testSession) {
		if (_listeners == null || _listeners.size() == 0) {
			return;
		}
		TestSessionListener[] listeners = _listeners.toArray(new TestSessionListener[_listeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].messageReceived(message, testSession);
		}
	}

	public boolean hasPassedAllTests() {
		boolean passed = true;
		for (InMessage message : getIncomingMessages()) {
			passed = passed && message.hasPassedAllTests();
			if (!passed) {
				return passed;
			}
		}
		for (OutMessage outMessage : getOutgoingMessages()) {
			passed = passed && outMessage.hasPassedAllTests();
			if (!passed) {
				return passed;
			}
		}
		return passed;
	}
}