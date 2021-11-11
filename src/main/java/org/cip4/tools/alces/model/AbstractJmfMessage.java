package org.cip4.tools.alces.model;

import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.util.JDFConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract model object of a JMF Message.
 */
public abstract class AbstractJmfMessage {

    private String header;

    private String body;

    private String contentType;

    private List<TestResult> testResults;

    private boolean isSessionInitiator = false;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param contentType        The content type of the message.
     * @param header             The http header of the message.
     * @param body               The message body.
     * @param isSessionInitiator flag if message is a session initiator.
     */
    public AbstractJmfMessage(String contentType, String header, String body, boolean isSessionInitiator) {
        this.contentType = contentType;
        this.header = header;
        this.body = body;
        this.isSessionInitiator = isSessionInitiator;
        this.testResults = new ArrayList<>();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public boolean isSessionInitiator() {
        return isSessionInitiator;
    }

    public void setSessionInitiator(boolean sessionInitiator) {
        isSessionInitiator = sessionInitiator;
    }

    public boolean hasPassedAllTests() {
        boolean passed;

        for (TestResult testResult : this.getTestResults()) {
            passed = testResult.isPassed() || testResult.isIgnored();

            if (!passed) {
                return false;
            }
        }

        return true;
    }
}
