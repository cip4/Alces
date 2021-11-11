/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.message;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a <code>Message</code>. This class wraps a non-Swing implementation of a Message. The wrapped Message
 * contains the logic, this class is simply a wrapper so that the Message can be displayed in a Swing JTree.
 */
public abstract class AbstractMessageNode extends DefaultMutableTreeNode {

	protected static Logger log = LoggerFactory.getLogger(AbstractMessageNode.class);

	private final AbstractJmfMessage jmfMessage;
	private final DefaultTreeModel treeModel;

	/**
	 * Creates a new Message that is a TreeNode and wraps a non-Swing Message implementation. The wrapped Message contains the logic, this class is simply a
	 * wrapper so that the Message can be displayed in a JTree.
	 * @param jmfMessage The Message to wrap. This object may not be an instance of TreeNode.
	 * @param treeModel
	 */
	public AbstractMessageNode(AbstractJmfMessage jmfMessage, DefaultTreeModel treeModel) {
		super();

		this.jmfMessage = jmfMessage;
		this.treeModel = treeModel;

		final StringBuffer nodeLabel = new StringBuffer();

		// Builds the nodes label using all child messages
		final JDFJMF jmf = JmfUtil.getBodyAsJMF(jmfMessage);
		if (jmf != null) {
			final NodeList nodes = jmf.getJMFRoot().getChildNodes();
			for (int i = 0, imax = nodes.getLength(); i < imax; i++) {
				final Node node = nodes.item(i);
				if (node instanceof JDFElement) {
					final JDFElement msg = (JDFElement) node;
					nodeLabel.append(msg.getTagName());
					nodeLabel.append(msg.getAttribute("Type"));
					nodeLabel.append(" ");
				}
			}
		} else {
			nodeLabel.append(jmfMessage.getContentType());
		}
		setUserObject(nodeLabel.toString());
	}

	public AbstractJmfMessage getJmfMessage() {
		return jmfMessage;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	//	/**
//	 * Wraps a TestResult in a TestResultNode and adds it to the Message and to the TestSuite's TreeModel.
//	 */
//	public void addTestResult(TestResult testResult) {
//		if (log.isDebugEnabled()) {
//			log.debug("Adding TestResult to message/tree: " + testResult);
//		}
//		final MutableTreeNode thisNode = this;
//		final MutableTreeNode testResultNode;
//		if (testResult instanceof MutableTreeNode) {
//			testResultNode = (MutableTreeNode) testResult;
//		} else {
//			testResultNode = new TestResultNode(testResult, _treeModel);
//		}
//		_wrappedMessage.addTestResult((TestResult) testResultNode);
//		// Update tree model using Swing's event-dispatching thread
//		Runnable addTestResult = new Runnable() {
//			public void run() {
//				log.debug("Inserting TestResult in tree model...");
//				_treeModel.insertNodeInto(testResultNode, thisNode, thisNode.getChildCount());
//				log.debug("Inserted TestResult in tree model.");
//			}
//		};
//		log.debug("Queueing TestResult for insertion in tree model...");
//		SwingUtilities.invokeLater(addTestResult);
//	}


}
