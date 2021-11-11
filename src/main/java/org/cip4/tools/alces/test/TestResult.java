/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.test;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.test.tests.Test;

/**
 * A class that represents the result of running a <code>Test</code> on a
 * <code>Message</code>.
 * 
 * @author Claes Buckwalter
 */
public interface TestResult {

	public enum Result {
		PASSED, FAILED, IGNORED;

		/**
		 * @param passed	the result of the test
		 * @return {@link #PASSED} if passed is <code>true</code>; {@link #FAILED} otherwise
		 */
		public static Result getPassed(boolean passed) {
			Result result;
			if (passed) {
				result = PASSED;
			} else {
				result = FAILED;
			}
			return result;
		}
	}

	/**
	 * Returns a plain text description of this test result, for example a list
	 * or errors.
	 * 
	 * @return a plain text description of this test result
	 */
	public String getResultString();

	/**
	 * Returns the Message tested by the Test.
	 * 
	 * @return
	 */
	public AbstractJmfMessage getMessage();

	/**
	 * Returns <code>true</code> if the <code>Message</code> passed the
	 * <code>Test</code>.
	 * 
	 * @return <code>true</code> if the <code>Test</code> passed;
	 *         <code>false</code> otherwise
	 */
	public boolean isPassed();

	/**
	 * Returns <code>true</code> if the <code>Test</code> was not applied to
	 * the <code>Message</code>.
	 * 
	 * @return <code>true</code> if the <code>Test</code> was no applied to
	 *         the <code>Message</code>
	 */
	public boolean isIgnored();

	/**
	 * Returns the test result.
	 * 
	 * @return {@link Result#PASSED} if the test passed; {@link Result#FAILED}
	 *         if the test failed; {@link Result#IGNORED} if the test was
	 *         ignored
	 */
	public Result getResult();

	/**
	 * Returns the Test that produced this TestResult.
	 * 
	 * @return
	 */
	public Test getTest();
}