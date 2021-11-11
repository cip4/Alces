package org.cip4.tools.alces.swingui.tree.test;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.swingui.tree.message.InMessageNode;
import org.cip4.tools.alces.swingui.tree.message.OutMessageNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSessionListener;
import org.cip4.tools.alces.test.tests.Test;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a <code>TestSession</code>. This class wraps a non-Swing TestSession implementation. The wrapped
 * TestSession contains the logic, this class is simply a wrapper allowing the TestSession to be displayed in a Swing JTree.
 */
public class TestSessionNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 865715767294195142L;

	protected static Logger log = LoggerFactory.getLogger(TestSessionNode.class);

	private TestSession testSession;

	public boolean valid;

	private Executor _executor;

	private boolean _asynchronous;

	private DefaultTreeModel _treeModel;

	/**
	 * Creates an TestSession that is a TreeNode and wraps a non-Swing TestSession implementation. The wrapped TestSession contains the logic, this class is
	 * simply a wrapper allowing the TestSession to be displayed in a JTree.
	 * @param testSession The TestSession to wrap. This object may not be an instance of TreeNode.
	 * @param treeModel the tree model this node belongs to
	 */
	public TestSessionNode(TestSession testSession, DefaultTreeModel treeModel) {
		this(testSession, treeModel, true);
	}

	/**
	 * Creates an TestSession that is a TreeNode and wraps a non-Swing TestSession implementation. The wrapped TestSession contains the logic, this class is
	 * simply a wrapper allowing the TestSession to be displayed in a JTree.
	 * @param testSession The TestSession to wrap. This object may not be an instance of TreeNode.
	 * @param treeModel the tree model this node belongs to
	 * @param asyncMode if true messages are queued and sent asynchronously; if false messages are sent synchronously
	 */
	public TestSessionNode(TestSession testSession, DefaultTreeModel treeModel, boolean asyncMode) {
		if (testSession instanceof TreeNode) {
			throw new IllegalArgumentException("The TestSession may not be a TreeNode.");
		}
		_asynchronous = asyncMode;
		_treeModel = treeModel;
		this.testSession = testSession;
		_executor = new ThreadedExecutor();
		setUserObject(testSession.getTargetUrl());
	}

	public String getTargetUrl() {
		return testSession.getTargetUrl();
	}

	public void setTargetUrl(String targetUrl) {
		testSession.setTargetUrl(targetUrl);
	}

	/**
	 * Wraps the <code>OutMessage</code> in a <code>OutMessageNode</code> if necessary, then adds the message as a direct child to this tree node if it is an
	 * initiating message.
	 */
	public void sendMessage(final OutgoingJmfMessage message) {
		if (_asynchronous) {
			log.debug("Sending message asynchronously...");
			try {
				_executor.execute(() -> sendMessageSync(message));
				log.debug("Message sent asynchronously.");
			} catch (InterruptedException ie) {
				log.error("Could not send message asynchronously: " + message, ie);
			}
		} else {
			log.debug("Sending message synchronously...");
			sendMessageSync(message);
			log.debug("Sent message synchronously.");
		}
	}

	/**
	 * Sends a message synchronously. In other words, blocks while sending.
	 * @param message
	 * @retn
	 */
	public void sendMessageSync(OutgoingJmfMessage message) {
		final MutableTreeNode thisNode = this;
		final MutableTreeNode messageNode;
		if (message instanceof MutableTreeNode) {
			messageNode = (MutableTreeNode) message;
		} else {
			log.debug("Wrapping OutMessage in OutMessageNode...");
			messageNode = new OutMessageNode(message, _treeModel);
		}
		if (message.isSessionInitiator()) {
			log.debug("Adding initiating OutMessage to TestSession...");
			setUserObject(messageNode + " - " + getTargetUrl());

			// Update tree model using Swing's event-dispatching thread
			Runnable addOutMessage = () -> {
				log.debug("Inserting OutMessage as child to TestSession in tree model...");
				_treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
				log.debug("Inserted OutMessage in tree model.");
			};
			log.debug("Queueing OutMessage for insertion as child to TestSession in tree model...");
			SwingUtilities.invokeLater(addOutMessage);
		}
		testSession.sendMessage((OutgoingJmfMessage) messageNode);
	}

	/**
	 * Wraps the <code>InMessage</code> in a <code>InMessageNode</code> if necessary, then adds the InMessage as a direct child to this tree node if it is an
	 * initiating message.
	 * @param message the incoming message to receive
	 */
	public void receiveMessage(IncomingJmfMessage message) {
		log.debug("Receiving InMessage...");
		final MutableTreeNode thisNode = this;
		final MutableTreeNode messageNode;
		if (!(message instanceof MutableTreeNode)) {
			log.debug("Wrapping InMessage in InMessageNode...");
			messageNode = new InMessageNode(message, _treeModel); // XXX
		} else {
			messageNode = (MutableTreeNode) message;
		}
		if (message.isSessionInitiator()) {
			log.debug("Adding initiating InMessage to TestSession...");
			// Update tree model using Swing's event-dispatching thread
			Runnable addMessage = new Runnable() {
				public void run() {
					log.debug("Inserting InMessage as child to TestSession in tree model...");
					_treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
					log.debug("Inserted InMessage in tree model.");
				}
			};
			log.debug("Queueing InMessage for insertion as child to TestSession in tree model...");
			SwingUtilities.invokeLater(addMessage);
		}
		testSession.receiveMessage(message);
		setUserObject(getInitiatingMessage() + " - " + getTargetUrl()); // XXX invokeLater
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addOutgoingTest(org.cip4.tools.alces.tests.Test)
	 */
	public void addOutgoingTest(Test test) {
		testSession.addOutgoingTest(test);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addIncomingTest(org.cip4.tools.alces.tests.Test)
	 */
	public void addIncomingTest(Test test) {
		testSession.addIncomingTest(test);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addTestResult(org.cip4.tools.alces.TestResult)
	 */
	public void addTestResult(TestResult testResult) {
		testSession.addTestResult(testResult);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getIncomingMessages()
	 */
	public List<IncomingJmfMessage> getIncomingMessages() {
		return testSession.getIncomingMessages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getOutgoingMessages()
	 */
	public List<OutgoingJmfMessage> getOutgoingMessages() {
		return testSession.getOutgoingMessages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getTestResults()
	 */
	public List<TestResult> getTestResults() {
		return testSession.getTestResults();
	}

	public OutgoingJmfMessage getOutgoingMessage(IncomingJmfMessage message) {
		return testSession.getOutgoingMessage(message);
	}

	public IncomingJmfMessage getIncomingMessage(OutgoingJmfMessage message) {
		return testSession.getIncomingMessage(message);
	}

	public AbstractJmfMessage getInitiatingMessage() {
		return testSession.getInitiatingMessage();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void addListener(TestSessionListener listener) {
		testSession.addListener(listener);
	}

	public void removeListener(TestSessionListener listener) {
		testSession.removeListener(listener);
	}

	public boolean hasPassedAllTests() {
		return testSession.hasPassedAllTests();
	}
}
