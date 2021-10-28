/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.message;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.swingui.tree.test.TestResultNode;
import org.cip4.tools.alces.test.TestResult;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a <code>Message</code>. This class wraps a non-Swing implementation of a Message. The wrapped Message
 * contains the logic, this class is simply a wrapper so that the Message can be displayed in a Swing JTree.
 * 
 * @see javax.swing.tree.DefaultMutableTreeNode
 * @see org.cip4.tools.alces.message.Message
 * @author Claes Buckwalter
 * @version $Id$
 */
public abstract class AbstractMessageNode extends DefaultMutableTreeNode implements Message {

	protected static Logger log = LoggerFactory.getLogger(AbstractMessageNode.class);

	protected Message _wrappedMessage = null;
	protected DefaultTreeModel _treeModel = null;

	/**
	 * Creates a new Message that is a TreeNode and wraps a non-Swing Message implementation. The wrapped Message contains the logic, this class is simply a
	 * wrapper so that the Message can be displayed in a JTree.
	 * @param message The Message to wrap. This object may not be an instance of TreeNode.
	 * @param treeModel
	 */
	public AbstractMessageNode(Message message, DefaultTreeModel treeModel) {
		super();
		if (message instanceof TreeNode) {
			throw new IllegalArgumentException("The Message may not be of type TreeNode.");
		}
		_wrappedMessage = message;
		_treeModel = treeModel;
		final StringBuffer nodeLabel = new StringBuffer();
		// Builds the nodes label using all child messages
		final JDFJMF jmf = message.getBodyAsJMF();
		if (jmf != null) {
			final NodeList nodes = jmf.getJMFRoot().getChildNodes(); // JDFElement msgs[] = jmf.getJMFRoot().getChildElements();
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
			nodeLabel.append(message.getContentType());
		}
		setUserObject(nodeLabel.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getBody()
	 */
	public String getBody() {
		return _wrappedMessage.getBody();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#setBody(java.lang.String)
	 */
	public void setBody(String body) {
		_wrappedMessage.setBody(body);

	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getBodyAsJDOM()
	 */
	public Document getBodyAsJDOM() {
		// TODO Auto-generated method stub
		return _wrappedMessage.getBodyAsJDOM();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getBodyAsJMF()
	 */
	public JDFJMF getBodyAsJMF() {
		// TODO Auto-generated method stub
		return _wrappedMessage.getBodyAsJMF();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getBodyAsJDF()
	 */
	public JDFNode getBodyAsJDF() {
		// TODO Auto-generated method stub
		return _wrappedMessage.getBodyAsJDF();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getHeader()
	 */
	public String getHeader() {
		// TODO Auto-generated method stub
		return _wrappedMessage.getHeader();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#setHeader(java.lang.String)
	 */
	public void setHeader(String header) {
		_wrappedMessage.setHeader(header);
	}

	public void setContentType(String contentType) {
		_wrappedMessage.setContentType(contentType);
	}

	public String getContentType() {
		return _wrappedMessage.getContentType();
	}

	/**
	 * Wraps a TestResult in a TestResultNode and adds it to the Message and to the TestSuite's TreeModel.
	 */
	public void addTestResult(TestResult testResult) {
		if (log.isDebugEnabled()) {
			log.debug("Adding TestResult to message/tree: " + testResult);
		}
		final MutableTreeNode thisNode = this;
		final MutableTreeNode testResultNode;
		if (testResult instanceof MutableTreeNode) {
			testResultNode = (MutableTreeNode) testResult;
		} else {
			testResultNode = new TestResultNode(testResult, _treeModel);
		}
		_wrappedMessage.addTestResult((TestResult) testResultNode);
		// Update tree model using Swing's event-dispatching thread
		Runnable addTestResult = new Runnable() {
			public void run() {
				log.debug("Inserting TestResult in tree model...");
				_treeModel.insertNodeInto(testResultNode, thisNode, thisNode.getChildCount());
				log.debug("Inserted TestResult in tree model.");
			}
		};
		log.debug("Queueing TestResult for insertion in tree model...");
		SwingUtilities.invokeLater(addTestResult);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.Message#getTestResults()
	 */
	public List<TestResult> getTestResults() {
		return _wrappedMessage.getTestResults();
	}

	public boolean isSessionInitiator() {
		return _wrappedMessage.isSessionInitiator();
	}
}
