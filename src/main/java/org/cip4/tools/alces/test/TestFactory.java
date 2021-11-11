/*
 * Created on Oct 20, 2005
 */
package org.cip4.tools.alces.test;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.tests.Test;

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
    IncomingJmfMessage createInMessage(String contentType, String header, String body, boolean isSessionInitiator);

    /**
     * Creates an outgoing message.
     * @param contentType
     * @param header
     * @param body
     * @param isSessionInitiator
     * @return
     */
    OutgoingJmfMessage createOutMessage(String contentType, String header, String body, boolean isSessionInitiator);

    /**
     * Creates a test session.
     * @param targetUrl
     * @return
     */
    TestSession createTestSession(String targetUrl);

    /**
     * Creates a test result.
     * @param test
     * @param testedMessage
     * @param testLog
     * @return
     */
    TestResult createTestResult(Test test, AbstractJmfMessage testedMessage, Result result, String testLog);

}