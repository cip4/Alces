package org.cip4.tools.alces.service.testrunner.model;

/**
 * Listener interface for TestSuite updates.
 */
public interface TestSuiteListener {

    /**
     * Is called in case the test suite has been updated.
     * @param testSuite The updated test suite.
     */
    void handleTestSuiteUpdate(TestSuite testSuite);
}
