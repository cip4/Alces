package org.cip4.tools.alces.service.testrunner.model;

import java.util.List;

/**
 * Listener interface for TestSession updates.
 */
public interface TestSessionsListener {

    /**
     * Is called in case the test sessions have been updated.
     * @param testSessions The updated full test sessions list.
     */
    void handleTestSessionsUpdate(List<TestSession> testSessions);
}
