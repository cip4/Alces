/*
 * Created on May 3, 2005
 */
package org.cip4.tools.alces.swingui.tree.test;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.swingui.tree.message.InMessageNode;
import org.cip4.tools.alces.swingui.tree.message.OutMessageNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSessionListener;
import org.cip4.tools.alces.test.TestSuite;
import org.cip4.tools.alces.test.TestSuiteImpl;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.tests.Test;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a
 * <code>TestSuite</code>. This class contains no logic but merely acts as a
 * wrapper for another TestSuite implementation. This class is also a factory
 * for creating TestSuite-related wrapper objects that are TreeNode.
 * 
 * @author Claes Buckwalter
 */
public class TestSuiteNode extends DefaultMutableTreeNode implements TestSuite {

    private static Log log = LogFactory.getLog(TestSuiteNode.class);

    private final TestSuite _wrappedTestSuite;
    private DefaultTreeModel _treeModel; 

    public TestSuiteNode() {
        super("Tests"); // XXX
        _wrappedTestSuite = new TestSuiteImpl();
    }

    /**
     * Adds the <code>TestSession</code> to the <code>TestSuite</code>. If
     * the <code>TestSession</code> is not a <code>MutableTreeNode</code> it
     * is wrapped in a <code>TestSessionNode</code> before being added to the
     * pool.
     * 
     * @return the <code>TestSessionNode</code> that was added to the pool
     * @see org.cip4.elk.alces.TestSuite#addTestSession(org.cip4.elk.alces.TestSessionImpl)
     */
    public TestSession addTestSession(TestSession testSession) {
        log.debug("Adding TestSession to tree/pool: " + testSession + " "
                + testSession.hashCode());
        final MutableTreeNode thisNode = this;
        final MutableTreeNode testSessionNode;
        // Wrap TestSession in TreeNode
        if (testSession instanceof MutableTreeNode) {
            testSessionNode = (MutableTreeNode) testSession;
        } else {
            testSessionNode = new TestSessionNode(testSession, _treeModel);
        }
        // Add TestSession to wrapped suite
        testSession = _wrappedTestSuite.addTestSession(testSession);
        // Add TestSession to tree
        Runnable addTestSession = new Runnable() {
            public void run() {
                log.debug("Inserting TestSession as child to TestSuite in tree model...");
                _treeModel.insertNodeInto(testSessionNode, thisNode, thisNode.getChildCount());
                log.debug("Inserted TestSession in tree model.");
            }
        };
        log.debug("Queueing TestSession for insertion as child to TestSuite in tree model...");
        SwingUtilities.invokeLater(addTestSession);
        return (TestSession) testSessionNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.elk.alces.TestSuite#removeTestSession(org.cip4.elk.alces.TestSessionImpl)
     */
    public boolean removeTestSession(TestSession testSession) {
        log.debug("Removing TestSession from tree/pool: " + testSession);
        remove((MutableTreeNode) testSession);
        return _wrappedTestSuite.removeTestSession(testSession);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.elk.alces.TestSuite#findTestSession(org.cip4.elk.alces.Message)
     */
    public TestSession findTestSession(Message message) {
        return _wrappedTestSuite.findTestSession(message);
    }

    /**
     * Sets the tree model this suite belongs too. A refernce of the tree model
     * is passed to all objects this factory creates. When new objects are added
     * to the suite they are also added to the tree model.
     * 
     * @param treeModel
     */
    public void setTreeModel(DefaultTreeModel treeModel) {
        _treeModel = treeModel;
    }

    /**
     * Returns the tree model this suite belongs to.
     * 
     * @return
     */
    public DefaultTreeModel getTreeModel() {
        return _treeModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cip4.elk.alces.TestSuite#getTestSessions()
     */
    public List<TestSession> getTestSessions() {
        return _wrappedTestSuite.getTestSessions();
    }

    public InMessage createInMessage(String contentType, String header,
            String body, boolean isSessionInitiator) {
        return new InMessageNode(_wrappedTestSuite.createInMessage(contentType,
            header, body, isSessionInitiator), _treeModel);
    }

    public OutMessage createOutMessage(String contentType, String header,
            String body, boolean isSessionInitiator) {
        return new OutMessageNode(_wrappedTestSuite.createOutMessage(
            contentType, header, body, isSessionInitiator), _treeModel);
    }

    public TestSession createTestSession(String targetUrl) {
        return new TestSessionNode(_wrappedTestSuite
                .createTestSession(targetUrl), _treeModel);
    }

    public TestResult createTestResult(Test test, Message testedMessage,
            Result result, String testLog) {
        return new TestResultNode(_wrappedTestSuite.createTestResult(test,
            testedMessage, result, testLog), _treeModel);
    }

	public void addTestSessionListener(TestSessionListener listener) {
		_wrappedTestSuite.addTestSessionListener(listener);		
	}

	public void removeTestSessionListener(TestSessionListener listener) {
		_wrappedTestSuite.removeTestSessionListener(listener);		
	}
}
