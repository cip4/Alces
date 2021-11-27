package org.cip4.tools.alces.service.testrunner;

import org.cip4.tools.alces.service.testrunner.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Business interface for a test-runner.
 */
public interface TestRunnerService {

    /**
     * Register a new TestSuite listener.
     *
     * @param testSessionsListener The TestSuite lister.
     */
    void registerTestSuiteListener(TestSessionsListener testSessionsListener);

    /**
     * Returns a list of all active test sessions.
     *
     * @return List of active test sessions.
     */
    List<TestSession> getTestSessions();

    /**
     * Clear all test sessions.
     */
    void clearTestSessions();

    /**
     * Clear the given test sessions.
     *
     * @param testSession The test session to be removed.
     */
    void clearTestSession(TestSession testSession);


    /**
     * Process an incoming jmf message. The message is being attached to the test session it belongs to.
     * If no test session exists, a new one is being created.
     *
     * @param incomingJmfMessage The incoming jmf message.
     * @param jmfEndpointUrl     The senders jmf endpoint url.
     */
    void processIncomingJmfMessage(IncomingJmfMessage incomingJmfMessage, String jmfEndpointUrl);

    /**
     * Starts a new test session based on an outgoing jmf message and a target url.
     *
     * @param jmfMessage The JMF Message as String.
     * @param targetUrl  The target URL the JMF needs to be sent to.
     * @return The created test session.
     */
    TestSession startTestSession(String jmfMessage, String targetUrl);

    /**
     * Starts a new test session based on an outgoing jmf message and a target url.
     *
     * @param outgoingJmfMessage The outgoing JMF Message as String.
     * @param targetUrl          The target URL the JMF needs to be sent to.
     * @return The created test session.
     */
    @Deprecated
    TestSession startTestSession(OutgoingJmfMessage outgoingJmfMessage, String targetUrl);

    /**
     * Loads a message from a file.
     *
     * @param file The file to be sent.
     * @return The file as outgoing jmf message.
     */
    @Deprecated
    OutgoingJmfMessage loadMessage(File file);

    /**
     * Starts a new test session by sending a SubmitQueueEntry JMF message that refers to the specified JDF file. Before submitting the JDF job, the JDF file is
     * published to this TestRunners HTTP server and the resulting http URL is used in the SubmitQueueEntry JMF message.
     * @param jdfFile       the JDF file to submit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf <code>true</code> to preprocess the JDF file before submitting it; <code>false</code> otherwise
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    @Deprecated
    TestSession startTestSessionWithSubmitQueueEntry(File jdfFile, String targetUrl, boolean preprocessJdf, boolean asMime);

    /**
     * Starts a new test session by sending a ResubmitQueueEntry JMF message. that refers to the specified JDF file.
     * If asMime is true the JDF file, its content files, and the ResubmitQueueEntry JMF message are bundled in a MIME package.
     * If asMime is false, the JDF job, the JDF file is published to this TestRunners HTTP server and the resulting http URL is used
     * in the ResubmitQueueEntry JMF message.
     * @param jdfFile       the JDF file to submit
     * @param queueEntryId  the queue entry ID of the job to resubmit
     * @param jobId         the /JDF/@JobID of the job to resubmit
     * @param targetUrl     the URL to submit it to
     * @param preprocessJdf
     * @param asMime        packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    @Deprecated
    TestSession startTestSessionWithResubmitQueueEntry(File jdfFile, String queueEntryId, String jobId, String targetUrl, boolean preprocessJdf, boolean asMime);
}
