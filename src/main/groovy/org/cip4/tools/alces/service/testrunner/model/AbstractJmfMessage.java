package org.cip4.tools.alces.service.testrunner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract model object of a JMF Message.
 */
public abstract class AbstractJmfMessage {

    private String body;

    private String contentType;

    private List<TestResult> testResults;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param contentType        The content type of the message.
     * @param body               The message body.
     */
    public AbstractJmfMessage(String contentType, String body) {
        this.contentType = contentType;
        this.body = body;
        this.testResults = new ArrayList<>();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public boolean hasPassedAllTests() {
        boolean passed;

        for (TestResult testResult : this.getTestResults()) {
            passed = testResult.getResult()  == TestResult.Result.PASSED || testResult.getResult()  == TestResult.Result.IGNORED;

            if (!passed) {
                return false;
            }
        }

        return true;
    }
}
