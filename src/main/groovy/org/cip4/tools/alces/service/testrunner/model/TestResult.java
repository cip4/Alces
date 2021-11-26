package org.cip4.tools.alces.service.testrunner.model;

import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;

/**
 * Test result model object.
 */
public class TestResult {

    private final Result result;
    private final String resultBody;

    private final AbstractJmfMessage message;
    private final JmfTest jmfTest;

    /**
     * Enum representing the test results.
     */
    public enum Result {
        PASSED, FAILED, IGNORED;
    }

    /**
     * Custom constructor. Accepting multiple parameters for initializing.
     * @param jmfTest The test which has been applied.
     * @param message The JMF Message.
     * @param result The machine-readable test result.
     * @param resultBody the human-readable test result body.
     */
    public TestResult(JmfTest jmfTest, AbstractJmfMessage message, Result result, String resultBody) {
        this.jmfTest = jmfTest;
        this.message = message;
        this.result= result;
        this.resultBody = resultBody;
    }

    public String getResultBody() {
        return resultBody;
    }

    public Result getResult() {
        return result;
    }

    public AbstractJmfMessage getMessage() {
        return message;
    }

    public JmfTest getJmfTest() {
        return jmfTest;
    }

}
