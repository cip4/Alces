package org.cip4.tools.alces.test.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResult;

/**
 * A test for testing <code>Message</code>s.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public abstract class Test {

    protected static Log log = LogFactory.getLog(Test.class);
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

    /**
     * Runs this test on a <code>Message</code>.
     * 
     * @param message the <code>Message</code> to test
     * @return a <code>TestResult</code> that describes the result of the test
     */
    public abstract TestResult runTest(Message message);

    @Override
	public String toString() {
        return description;
    }

}
