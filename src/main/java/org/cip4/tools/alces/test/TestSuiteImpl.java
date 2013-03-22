/*
 * Created on Apr 24, 2005
 */
package org.cip4.tools.alces.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.tests.Test;

/**
 * Alces' default implementation of <code>TestSuite</code>.
 * 
 * @author Claes Buckwalter
 */
public class TestSuiteImpl implements TestSuite, TestSessionListener {

	private static Log log = LogFactory.getLog(TestSuiteImpl.class);

	private final List<TestSession> _testSessions;
	
	private final List<TestSessionListener> _listeners;

	public TestSuiteImpl() {
		_testSessions = new Vector<TestSession>();
		_listeners = new ArrayList<TestSessionListener>();
	}

	/**
	 * Adds a TestSession to the pool of active TestSessions.
	 * 
	 * @param testSession
	 */
	public synchronized TestSession addTestSession(TestSession testSession) {
		_testSessions.add(testSession);
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
		return _testSessions.remove(testSession);
	}

	/**
	 * Finds the TestSession that the messages belongs to.
	 * 
	 * @param message
	 * @return
	 */
	public synchronized TestSession findTestSession(Message message) {
		final JDFJMF jmf = message.getBodyAsJMF();
		if (jmf == null) {
			log.debug("Incoming message does not contain JMF; TestSession not found.");
			return null;
		}
		final JDFMessage jmfMsg = jmf.getMessageElement(null, null, 0);
		final String refId = jmfMsg.getrefID();
		log.debug("Searching for test session for incoming JMF message with refID '" + refId
				+ "'...");
		for (TestSession s : _testSessions) {			
			for (Message mOut : s.getOutgoingMessages()) {				
				JDFJMF jmfOut = mOut.getBodyAsJMF();
				if (jmfOut == null) {
					log.debug("TestSession's outgoing message does not contain JMF; incoming message cannot be matched to outgoing message.");
					continue;
				}
				JDFMessage jmfMsgOut = jmfOut.getMessageElement(null, null, 0);
				if (refId.startsWith(jmfMsgOut.getID())) {
					log.debug("Found test session with refID '" + jmfMsgOut.getID()
							+ "' that matches incoming message with refID '" + refId + "'.");
					return s;
				}
			}
		}
		log.warn("No test session was found that matches incoming JMF message with refID '"
				+ refId + "'.");
		return null;
	}

	public List<TestSession> getTestSessions() {
		return _testSessions;
	}

	public InMessage createInMessage(String contentType, String header, String body,
			boolean isSessionInitiator) {
		return new InMessageImpl(contentType, header, body, isSessionInitiator);
	}

	public OutMessage createOutMessage(String contentType, String header, String body,
			boolean isSessionInitiator) {
		return new OutMessageImpl(contentType, header, body, isSessionInitiator);
	}

	public TestSession createTestSession(String targetUrl) {
		return new TestSessionImpl(targetUrl);
	}

	public TestResult createTestResult(Test test, Message testedMessage, Result result,
			String testLog) {
		return new TestResultImpl(test, testedMessage, result, testLog);
	}

	// TestSession listeners
	
	public void addTestSessionListener(TestSessionListener listener) {
		_listeners.add(listener);
	}

	public void removeTestSessionListener(TestSessionListener listener) {
		_listeners.remove(listener);
	}
	
	public void messageReceived(InMessage inMessage, TestSession testSession) {
		notifyListeners(inMessage, testSession);
	}
	
	/**
	 * SNotifies listeners that a new message has been received.
	 * 
	 * @param message
	 *            the received message
	 * @param testSession
	 *            the message's test session
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
	
}
