/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.service.testrunner.model;

import org.cip4.tools.alces.model.IncomingJmfMessage;

public interface TestSessionListener {

	/**
	 * Called when a <code>TestSession</code> receives an incoming message.
	 * 
	 * @param inMessage
	 *            the message received
	 * @param testSession
	 *            the test session the message belongs to
	 */
	void messageReceived(IncomingJmfMessage inMessage, TestSession testSession);
}
