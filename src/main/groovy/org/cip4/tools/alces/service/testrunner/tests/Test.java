package org.cip4.tools.alces.service.testrunner.tests;

import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;

/**
 * A test for testing <code>Message</code>s.
 */
public abstract class Test implements JmfTest {

	protected String description = null;

	public Test() {
		this("No description available.");
	}

	/**
	 * Creates a new <code>Test</code> with the specified description.
	 * 
	 * @param testDescription a description of the test
	 */
	public Test(String testDescription) {
		description = testDescription;
	}

	/**
	 * Returns a description of the test.
	 * 
	 * @return a description of the test
	 */
	public String getDescription() {
		return description;
	}

	public Type getType() {
		return null;
	}

	/**
	 * Runs this test on a <code>Message</code>.
	 * 
	 * @param message the <code>Message</code> to test
	 * @return a <code>TestResult</code> that describes the result of the test
	 */
	public abstract TestResult runTest(AbstractJmfMessage message);

	@Override
	public String toString() {
		return description;
	}

}
