/*
 * Created on May 3, 2005
 */
package org.cip4.tools.alces.test;

import java.util.List;

import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.test.tests.Test;
import org.cip4.tools.alces.transport.HttpDispatcher;

/**
 * A class that represents a test session. A test session consists of a set of
 * related messages that are sent and received and on which tests are run.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public interface TestSession {

	/**
	 * Sets the URL this test cases is target at.
	 * 
	 * @param targetUrl
	 */
	public void setTargetUrl(String targetUrl);

	/**
	 * Returns this test case's target URL.
	 * 
	 * @return
	 */
	public String getTargetUrl();

	/**
	 * Called when a new message is sent by this test session.
	 * 
	 * @param message
	 *            the message to send
	 * @param dispatcher
	 *            the dispatcher to use to send the message
	 */
	public void sendMessage(OutMessage message, HttpDispatcher dispatcher);

	/**
	 * Called when a new message is received by this test session.
	 * 
	 * @param message
	 */
	public void receiveMessage(InMessage message);

	/**
	 * Adds a test to perform on outgoing messages.
	 * 
	 * @param test
	 */
	public void addOutgoingTest(Test test);

	/**
	 * Adds a test to perform on incoming messages.
	 * 
	 * @param test
	 */
	public void addIncomingTest(Test test);

	/**
	 * Adds a result generated by an outgoing or incoming test.
	 * 
	 * @param testResult
	 */
	public void addTestResult(TestResult testResult);

	/**
	 * Gets a <code>List</code> of <code>InMessage</code>s that have been
	 * received during this <code>TestSession</code>.
	 */
	public List<InMessage> getIncomingMessages();

	/**
	 * Returns the incoming message (<code>InMessage</code>) that the
	 * specified outgoing message (<code>OutMessage</code>) is a response
	 * to.
	 * 
	 * @param message
	 *            the incoming message
	 * @return the incoming message that the outgoing message is a response to;
	 *         <code>null</code> if no incoming message can be found
	 */
	public InMessage getIncomingMessage(OutMessage message);

	/**
	 * Returns a <code>List</code> of <code>OutMessage</code>s that have
	 * been sent during this <code>TestSession</code>.
	 */
	public List<OutMessage> getOutgoingMessages();

	/**
	 * Returns the outgoing message (<code>OutMessage</code>) that the
	 * specified incoming message (<code>InMessage</code>) is a response to.
	 * 
	 * @param message
	 *            the incoming message
	 * @return the outgoing message that the incoming message is a response to;
	 *         <code>null</code> if the incoming message is cannot be match to
	 *         any outgoing message
	 */
	public OutMessage getOutgoingMessage(InMessage message);

	/**
	 * Gets a list of all the test results associated with this test session.
	 * 
	 * @return
	 */
	public List<TestResult> getTestResults();

	public Message getInitiatingMessage();

	/**
	 * Adds a listener to this TestSession. Each time a new message is received
	 * by this TestSession the listener will be notified.
	 * 
	 * @param listener
	 */
	public void addListener(TestSessionListener listener);

	/**
	 * Removes the listener from this TestSession.
	 * 
	 * @param listener
	 */
	public void removeListener(TestSessionListener listener);

	/**
	 * Returns true if all <code>Message</code>s sent/received during this
	 * <code>TestSession</code> have passed all their <code>Test</code>s.
	 * 
	 * @return <code>true</code> if all <code>Test</code>s have passed;
	 *         <code>false</code> otherwise
	 */
	public boolean hasPassedAllTests();
}