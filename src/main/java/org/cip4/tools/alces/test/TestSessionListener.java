/*
 * Created on Apr 10, 2007
 */
package org.cip4.tools.alces.test;

import org.cip4.tools.alces.message.InMessage;

public interface TestSessionListener {

	/**
	 * Called when a <code>TestSession</code> receives an incoming message.
	 * 
	 * @param inMessage
	 *            the message received
	 * @param testSession
	 *            the test session the message belongs to
	 */
	public void messageReceived(InMessage inMessage, TestSession testSession);
}
