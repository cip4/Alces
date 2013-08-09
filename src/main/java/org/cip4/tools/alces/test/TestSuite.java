/*
 * Created on May 3, 2005
 */
package org.cip4.tools.alces.test;

import java.util.List;

import org.cip4.tools.alces.message.Message;

/**
 * A class that represents a collection of <code>TestSession</code>s acts as
 * a factory for test-related objects.
 * 
 * @author Claes Buckwalter
 */
public interface TestSuite extends TestFactory {

	/**
	 * Adds a <code>TestSession</code> to this suite.
	 * 
	 * @param testSession
	 */
	public TestSession addTestSession(TestSession testSession);

	/**
	 * Removes a <code>TestSession</code> from the pool of active
	 * <code>TestSession</code>s.
	 * 
	 * @param testSession
	 * @return
	 */
	public boolean removeTestSession(TestSession testSession);

	/**
	 * Finds the <code>TestSession</code> that the messages belongs to.
	 * 
	 * @param message
	 * @return the <code>TestSession</code> that the <code>Message</code>
	 *         belongs to; <code>null</code> if the <code>Message</code> has
	 *         no <code>TestSession</code>
	 */
	public TestSession findTestSession(Message message);

	public List<TestSession> getTestSessions();
	
	public void addTestSessionListener(TestSessionListener listener);
	
	public void removeTestSessionListener(TestSessionListener listener);
}