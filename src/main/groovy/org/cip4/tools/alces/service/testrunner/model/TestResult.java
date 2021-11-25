package org.cip4.tools.alces.service.testrunner.model;

import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;
import org.cip4.tools.alces.service.testrunner.tests.Test;

/**
 * Test result model object.
 */
public class TestResult {

    private final String resultString;
    private final Result result;
    private final AbstractJmfMessage message;
    private final JmfTest jmfTest;

    public enum Result {
        PASSED, FAILED, IGNORED;
    }

    /**
     * Custom constructor. Accepting multiple parameters for initializing.
     * @param jmfTest The test which has been applied.
     * @param message The JMF Message.
     * @param result The machine-readable test result.
     * @param resultString the human-readable test result.
     */
    public TestResult(JmfTest jmfTest, AbstractJmfMessage message, Result result, String resultString) {
        this.jmfTest = jmfTest;
        this.message = message;
        this.result= result;
        this.resultString = resultString;
    }

    public String getResultString() {
        return resultString;
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
