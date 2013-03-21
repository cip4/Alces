/*
 * Created on Oct 20, 2005
 */
package org.cip4.elk.alces.test;

import org.cip4.elk.alces.message.InMessage;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.message.OutMessage;
import org.cip4.elk.alces.test.TestResult.Result;
import org.cip4.elk.alces.test.tests.Test;

/**
 * A factory for creating test-related objects.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public interface TestFactory {

    /**
     * Creates and incoming message.
     * @param contentType
     * @param header
     * @param body
     * @param isSessionInitiator
     * @return
     */
    public InMessage createInMessage(String contentType, String header, String body, boolean isSessionInitiator);

    /**
     * Creates an outgoing message.
     * @param contentType
     * @param header
     * @param body
     * @param isSessionInitiator
     * @return
     */
    public OutMessage createOutMessage(String contentType, String header, String body, boolean isSessionInitiator);

    /**
     * Creates a test session.
     * @param targetUrl
     * @return
     */
    public TestSession createTestSession(String targetUrl);

    /**
     * Creates a test result.
     * @param test
     * @param testedMessage
     * @param passedTest
     * @param testLog
     * @return
     */
    public TestResult createTestResult(Test test, Message testedMessage, Result result, String testLog);

}