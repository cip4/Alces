package org.cip4.tools.alces.service.testrunner.jmftest;

import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;

/**
 * Interface for an JMF Test
 */
public interface JmfTest {

    enum Type {
        JMF_IN_TEST, JMF_OUT_TEST, JMF_BOTH_TEST
    }

    Type getType();

    /**
     * Returns a human-readable description of the test.
     *
     * @return The human-readable description of the test
     */
    String getDescription();

    /**
     * Applies a test on a jmf message.
     *
     * @param message The jmf message to be tested.
     * @return A test result that describes the result of the test.
     */
    TestResult runTest(AbstractJmfMessage message);
}
