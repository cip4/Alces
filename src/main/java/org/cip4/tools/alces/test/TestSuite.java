package org.cip4.tools.alces.test;

import java.util.ArrayList;
import java.util.List;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.tests.Test;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alces's default implementation of TestSuite.
 */
public class TestSuite implements TestSessionListener {

	private static Logger log = LoggerFactory.getLogger(TestSuite.class);

	private final List<TestSession> testSessions;

	private final List<TestSessionListener> testSessionListeners;

	/**
	 * Default constructor.
	 */
	public TestSuite() {
		testSessions = new ArrayList<>();
		testSessionListeners = new ArrayList<>();
	}

	/**
	 * Adds a TestSession to the pool of active TestSessions.
	 * 
	 * @param testSession
	 */
	public synchronized TestSession addTestSession(TestSession testSession) {
		testSessions.add(testSession);
		testSession.addListener(this);
		return testSession;
	}

	/**
	 * Removes a TestSession from the pool of active TestSessions.
	 * 
	 * @param testSession
	 * @return
	 */
	public synchronized boolean removeTestSession(TestSession testSession) {
		return testSessions.remove(testSession);
	}

	/**
	 * Finds the TestSession that the messages belongs to.
	 * 
	 * @param message
	 * @return
	 */
	public synchronized TestSession findTestSession(AbstractJmfMessage message) {
		final JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
		if (jmf == null) {
			log.debug("Incoming message does not contain JMF; TestSession not found.");
			return null;
		}
		final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
		final String refId = jmfMsg.getrefID();
		log.debug("Searching for test session for incoming JMF message with refID '" + refId + "'...");
		for (TestSession testSession : testSessions) {
			for (AbstractJmfMessage mOut : testSession.getOutgoingMessages()) {
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

	public List<TestSession> getTestSessions() {
		return testSessions;
	}

	public IncomingJmfMessage createInMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		return new IncomingJmfMessage(contentType, header, body, isSessionInitiator);
	}

	public OutgoingJmfMessage createOutMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		return new OutgoingJmfMessage(contentType, header, body, isSessionInitiator);
	}

	public TestSession createTestSession(String targetUrl) {
		return new TestSession(targetUrl);
	}

	public TestResult createTestResult(Test test, AbstractJmfMessage testedMessage, Result result, String testLog) {
		return new TestResult(test, testedMessage, result, testLog);
	}

	// TestSession listeners

	public void addTestSessionListener(TestSessionListener listener) {
		testSessionListeners.add(listener);
	}

	public void removeTestSessionListener(TestSessionListener listener) {
		testSessionListeners.remove(listener);
	}

	public void messageReceived(IncomingJmfMessage inMessage, TestSession testSession) {
		notifyListeners(inMessage, testSession);
	}

	/**
	 * Notifies listeners that a new message has been received.
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

}
