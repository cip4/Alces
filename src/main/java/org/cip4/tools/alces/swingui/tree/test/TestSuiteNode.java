/*
 * Created on May 3, 2005
 */
package org.cip4.tools.alces.swingui.tree.test;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.cip4.tools.alces.swingui.tree.message.OutMessageNode;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSessionListener;
import org.cip4.tools.alces.test.TestSuite;
import org.cip4.tools.alces.test.TestSuiteImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a <code>TestSuite</code>. This class contains no logic but merely acts as a wrapper for another
 * TestSuite implementation. This class is also a factory for creating TestSuite-related wrapper objects that are TreeNode.
 */
public class TestSuiteNode extends DefaultMutableTreeNode {

	private static Logger log = LoggerFactory.getLogger(TestSuiteNode.class);

	private final TestSuite testSuite;
	private DefaultTreeModel treeModel;

	public TestSuiteNode() {
		super("Tests"); // XXX
		testSuite = new TestSuiteImpl();
	}

	public TestSuite getTestSuite() {
		return testSuite;
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

	public List<TestSession> getTestSessions() {
		return testSuite.getTestSessions();
	}


	public OutMessageNode createOutMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		return new OutMessageNode(testSuite.createOutMessage(contentType, header, body, isSessionInitiator), treeModel);
	}

	public void removeTestSessionListener(TestSessionListener listener) {
		testSuite.removeTestSessionListener(listener);
	}
}
