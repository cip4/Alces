package org.cip4.tools.alces.swingui.tree.test;

import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.swingui.tree.message.InMessageNode;
import org.cip4.tools.alces.swingui.tree.message.OutMessageNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSessionListener;
import org.cip4.tools.alces.test.TestSuite;
import org.cip4.tools.alces.test.tests.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DefaultMutableTreeNode implementation of a TestSuite. This class contains no logic but merely acts as a wrapper for another
 * TestSuite implementation. This class is also a factory for creating TestSuite-related wrapper objects that are TreeNode.
 */
public class TestSuiteTreeNode extends DefaultMutableTreeNode {

	private static Logger log = LoggerFactory.getLogger(TestSuiteTreeNode.class);

	private final TestSuite testSuite;
	private DefaultTreeModel treeModel;  // very, very ugly

	/**
	 * Private default constructor.
	 */
	public TestSuiteTreeNode(TestSuite testSuite) {
		super("Tests");
		this.testSuite = testSuite;
	}

	/**
	 * Adds the <code>TestSession</code> to the <code>TestSuite</code>. If the <code>TestSession</code> is not a <code>MutableTreeNode</code> it is wrapped in a
	 * <code>TestSessionNode</code> before being added to the pool.
	 *
	 * @return the <code>TestSessionNode</code> that was added to the pool
	 */
	public TestSession addTestSession(TestSession testSession) {
		log.debug("Adding TestSession to tree/pool: " + testSession + " " + testSession.hashCode());
		final MutableTreeNode thisNode = this;
		final MutableTreeNode testSessionNode;
		// Wrap TestSession in TreeNode
		if (testSession instanceof MutableTreeNode) {
			testSessionNode = (MutableTreeNode) testSession;
		} else {
			testSessionNode = new TestSessionNode(testSession, treeModel);
		}
		// Add TestSession to wrapped suite
		testSession = this.testSuite.addTestSession(testSession);
		// Add TestSession to tree
		Runnable addTestSession = new Runnable() {
			public void run() {
				log.debug("Inserting TestSession as child to TestSuite in tree model...");
				treeModel.insertNodeInto(testSessionNode, thisNode, thisNode.getChildCount());
				log.debug("Inserted TestSession in tree model.");
			}
		};
		log.debug("Queueing TestSession for insertion as child to TestSuite in tree model...");
		SwingUtilities.invokeLater(addTestSession);
		return (TestSession) testSessionNode;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSuite#removeTestSession(org.cip4.tools.alces.TestSessionImpl)
	 */
	public boolean removeTestSession(TestSession testSession) {
		log.debug("Removing TestSession from tree/pool: " + testSession);
		remove((MutableTreeNode) testSession);
		return this.testSuite.removeTestSession(testSession);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSuite#findTestSession(org.cip4.tools.alces.Message)
	 */
	public TestSession findTestSession(AbstractJmfMessage message) {
		return this.testSuite.findTestSession(message);
	}

	/**
	 * Sets the tree model this suite belongs too. A refernce of the tree model is passed to all objects this factory creates. When new objects are added to the
	 * suite they are also added to the tree model.
	 *
	 * @param treeModel
	 */
	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	/**
	 * Returns the tree model this suite belongs to.
	 *
	 * @return
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSuite#getTestSessions()
	 */
	public List<TestSession> getTestSessions() {
		return testSuite.getTestSessions();
	}

	public InMessageNode createInMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		return new InMessageNode(testSuite.createInMessage(contentType, header, body, isSessionInitiator), treeModel);
	}

	public OutMessageNode createOutMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		return new OutMessageNode(testSuite.createOutMessage(contentType, header, body, isSessionInitiator), treeModel);
	}

	public TestSessionNode createTestSession(String targetUrl) {
		return new TestSessionNode(testSuite.createTestSession(targetUrl), treeModel);
	}

	public TestResultNode createTestResult(Test test, AbstractJmfMessage testedMessage, TestResult.Result result, String testLog) {
		return new TestResultNode(testSuite.createTestResult(test, testedMessage, result, testLog), treeModel);
	}

	public void addTestSessionListener(TestSessionListener listener) {
		testSuite.addTestSessionListener(listener);
	}

	public void removeTestSessionListener(TestSessionListener listener) {
		testSuite.removeTestSessionListener(listener);
	}
}
