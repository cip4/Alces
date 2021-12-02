package org.cip4.tools.alces.service.testrunner;

import org.cip4.tools.alces.service.testrunner.model.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Business interface for a test-runner.
 */
public interface TestRunnerService {

    /**
     * Register a new TestSuite listener.
     * @param testSessionsListener The TestSuite lister.
     */
    void registerTestSuiteListener(TestSessionsListener testSessionsListener);

    /**
     * Returns a list of all active test sessions.
     * @return List of active test sessions.
     */
    List<TestSession> getTestSessions();

    /**
     * Clear all test sessions.
     */
    void clearTestSessions();

    /**
     * Clear the given test sessions.
     * @param testSession The test session to be removed.
     */
    void clearTestSession(TestSession testSession);


    /**
     * Process an incoming jmf message. The message is being attached to the test session it belongs to.
     * If no test session exists, a new one is being created.
     * @param incomingJmfMessage The incoming jmf message.
     * @param jmfEndpointUrl     The senders jmf endpoint url.
     */
    void processIncomingJmfMessage(IncomingJmfMessage incomingJmfMessage, String jmfEndpointUrl);

    /**
     * Starts a new test session based on an outgoing jmf message and a target url.
     * @param jmfMessage The JMF Message as String.
     * @param targetUrl  The target URL the JMF needs to be sent to.
     * @return The created test session.
     */
    Future<TestSession> startTestSession(String jmfMessage, String targetUrl);


}
