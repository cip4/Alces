package org.cip4.tools.alces.service.testrunner;

import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.cip4.tools.alces.service.testrunner.model.TestSuite;
import org.cip4.tools.alces.service.testrunner.model.TestSuiteListener;

import java.io.File;
import java.io.IOException;

/**
 * Business interface for a test-runner.
 */
public interface TestRunnerService {

    /**
     * Register a new TestSuite listener.
     * @param testSuiteListener The TestSuite lister.
     */
    void registerTestSuiteListener(TestSuiteListener testSuiteListener);

    /**
     * Returns the runners test suite.
     * @return The runners test suite.
     */
    TestSuite getTestSuite();

    /**
     * Clear all test sessions.
     */
    void clearTestSessions();

    /**
     * Clear the given test sessions.
     */
    void clearTestSession(TestSession testSession);

    /**
     * Starts a new test session based on a JMF Message and a traget url.
     * @param jmfMessage The JMF Message as String.
     * @param targetUrl The target URL the JMF needs to be sent to.
     * @return The created test session.
     */
    TestSession startTestSession(String jmfMessage, String targetUrl);

    @Deprecated
    TestSession startTestSession(OutgoingJmfMessage message, String targetUrl);

    /**
     * Sends a <code>OutMessage</code> to the preconfigured target URL. The message is preprocessed before it is sent.
     *
     * If <code>SenderIDPreprocessor</code> is enabled then <em>JMF/@SenderID</em> will be replaced with the value configured SenderID.
     *
     * @param message the <code>InMessage</code> received in the response
     * @return
     * @throws IOException if an communication exception occurs during the message sending
     */
    IncomingJmfMessage sendMessage(OutgoingJmfMessage message, String targetUrl) throws IOException;



    /**
     * Loads a message from a file.
     *
     * @param file
     * @return
     */
    OutgoingJmfMessage loadMessage(File file);

    /**
     * Starts a new test session by sending a SubmitQueueEntry JMF message that refers to the specified JDF file. Before submitting the JDF job, the JDF file is
     * published to this TestRunners HTTP server and the resulting http URL is used in the SubmitQueueEntry JMF message.
     *
     * @param jdfFile the JDF file to submit
     * @param targetUrl the URL to submit it to
     * @param preprocessJdf <code>true</code> to preprocess the JDF file before submitting it; <code>false</code> otherwise
     * @param asMime packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     *
     * @return the test session
     */
    TestSession startTestSessionWithSubmitQueueEntry(File jdfFile, String targetUrl, boolean preprocessJdf, boolean asMime);

    /**
     * Starts a new test session by sending a <i>ResubmitQueueEntry</i> JMF message. that refers to the specified JDF file.
     *
     * If <code>asMime</code> is <code>true</code> the JDF file, its content files, and the <i>ResubmitQueueEntry</i> JMF message are bundled in a MIME package.
     *
     * If <code>asMime</code> is <code>false</code>, the JDF job, the JDF file is published to this TestRunners HTTP server and the resulting http URL is used
     * in the <i>ResubmitQueueEntry</i> JMF message.
     *
     * @param jdfFile the JDF file to submit
     * @param queueEntryId the queue entry ID of the job to resubmit
     * @param jobId the /JDF/@JobID of the job to resubmit
     * @param targetUrl the URL to submit it to
     * @param preprocessJdf
     * @param asMime packages the JDF file, its content files, and the SubmitQueueEntry JMF in a MIME package
     * @return the test session
     */
    TestSession startTestSessionWithResubmitQueueEntry(File jdfFile, String queueEntryId, String jobId, String targetUrl, boolean preprocessJdf, boolean asMime);

    /**
     * Serializes the test suite, all incoming and outgoing messages to a directory and creates an XML-based test report file containing a log of all messages
     * and the test results.
     *
     * @param outputDir the directory to write the test suite to
     * @return the XML-based test report file
     * @throws IOException
     */
    String serializeTestSuite(String outputDir) throws IOException;
}
