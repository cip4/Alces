package org.cip4.tools.alces.swingui.tree.test;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.swingui.tree.message.InMessageNode;
import org.cip4.tools.alces.swingui.tree.message.OutMessageNode;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.test.TestSessionListener;
import org.cip4.tools.alces.test.tests.Test;
import org.cip4.tools.alces.transport.HttpDispatcher;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.ThreadedExecutor;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a <code>TestSession</code>. This class wraps a non-Swing TestSession implementation. The wrapped
 * TestSession contains the logic, this class is simply a wrapper allowing the TestSession to be displayed in a Swing JTree.
 * 
 * @author Claes Buckwalter
 */
public class TestSessionNode extends DefaultMutableTreeNode implements TestSession {

	private static final long serialVersionUID = 865715767294195142L;

	protected static Logger LOGGER = Logger.getLogger(TestSessionNode.class);

	private TestSession _wrappedTestSession = null;

	public boolean valid;

	private Executor _executor = null;

	private boolean _asynchronous = true;

	private DefaultTreeModel _treeModel = null;

	// /**
	// * Creates an TestSession that is a TreeNode and wraps a non-Swing TestSession
	// * implementation. The wrapped TestSession contains the logic, this class is simply
	// * a wrapper allowing the TestSession to be displayed in a JTree.
	// * @param treeModel the tree model this node belongs to
	// */
	// public TestSessionNode(String targetUrl, DefaultTreeModel treeModel) {
	// this(new TestSessionImpl(targetUrl), treeModel);
	// }

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
		_wrappedTestSession = testSession;
		_executor = new ThreadedExecutor();
		setUserObject(testSession.getTargetUrl());
	}

	public String getTargetUrl() {
		return _wrappedTestSession.getTargetUrl();
	}

	public void setTargetUrl(String targetUrl) {
		_wrappedTestSession.setTargetUrl(targetUrl);
	}

	/**
	 * Wraps the <code>OutMessage</code> in a <code>OutMessageNode</code> if necessary, then adds the message as a direct child to this tree node if it is an
	 * initiating message.
	 * 
	 * @see org.cip4.tools.alces.TestSession#sendMessage(org.cip4.tools.alces.OutMessage)
	 */
	public void sendMessage(final OutMessage message, final HttpDispatcher dispatcher) {
		if (_asynchronous) {
			LOGGER.debug("Sending message asynchronously...");
			try {
				_executor.execute(new Runnable() {
					public void run() {
						sendMessageSync(message, dispatcher);
					}
				});
				LOGGER.debug("Message sent asynchronously.");
			} catch (InterruptedException ie) {
				LOGGER.error("Could not send message asynchronously: " + message, ie);
			}
		} else {
			LOGGER.debug("Sending message synchronously...");
			sendMessageSync(message, dispatcher);
			LOGGER.debug("Sent message synchronously.");
		}
	}

	/**
	 * Sends a message synchronously. In other words, blocks while sending.
	 * @param message
	 * @retn
	 */
	public void sendMessageSync(OutMessage message, HttpDispatcher dispatcher) {
		final MutableTreeNode thisNode = this;
		final MutableTreeNode messageNode;
		if (message instanceof MutableTreeNode) {
			messageNode = (MutableTreeNode) message;
		} else {
			LOGGER.debug("Wrapping OutMessage in OutMessageNode...");
			messageNode = new OutMessageNode(message, _treeModel);
		}
		if (message.isSessionInitiator()) {
			LOGGER.debug("Adding initiating OutMessage to TestSession...");
			setUserObject(messageNode + " - " + getTargetUrl());
			// Update tree model using Swing's event-dispatching thread
			Runnable addOutMessage = new Runnable() {
				public void run() {
					LOGGER.debug("Inserting OutMessage as child to TestSession in tree model...");
					_treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
					LOGGER.debug("Inserted OutMessage in tree model.");
				}
			};
			LOGGER.debug("Queueing OutMessage for insertion as child to TestSession in tree model...");
			SwingUtilities.invokeLater(addOutMessage);
		}
		_wrappedTestSession.sendMessage((OutMessage) messageNode, dispatcher);
	}

	/**
	 * Wraps the <code>InMessage</code> in a <code>InMessageNode</code> if necessary, then adds the InMessage as a direct child to this tree node if it is an
	 * initiating message.
	 * @param message the incoming message to receive
	 * @see org.cip4.tools.alces.TestSession#receiveMessage(org.cip4.tools.alces.InMessage)
	 */
	public void receiveMessage(InMessage message) {
		LOGGER.debug("Receiving InMessage...");
		final MutableTreeNode thisNode = this;
		final MutableTreeNode messageNode;
		if (!(message instanceof MutableTreeNode)) {
			LOGGER.debug("Wrapping InMessage in InMessageNode...");
			messageNode = new InMessageNode(message, _treeModel); // XXX
		} else {
			messageNode = (MutableTreeNode) message;
		}
		if (message.isSessionInitiator()) {
			LOGGER.debug("Adding initiating InMessage to TestSession...");
			// Update tree model using Swing's event-dispatching thread
			Runnable addMessage = new Runnable() {
				public void run() {
					LOGGER.debug("Inserting InMessage as child to TestSession in tree model...");
					_treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
					LOGGER.debug("Inserted InMessage in tree model.");
				}
			};
			LOGGER.debug("Queueing InMessage for insertion as child to TestSession in tree model...");
			SwingUtilities.invokeLater(addMessage);
		}
		_wrappedTestSession.receiveMessage(message);
		setUserObject(getInitiatingMessage() + " - " + getTargetUrl()); // XXX invokeLater
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addOutgoingTest(org.cip4.tools.alces.tests.Test)
	 */
	public void addOutgoingTest(Test test) {
		_wrappedTestSession.addOutgoingTest(test);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addIncomingTest(org.cip4.tools.alces.tests.Test)
	 */
	public void addIncomingTest(Test test) {
		_wrappedTestSession.addIncomingTest(test);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#addTestResult(org.cip4.tools.alces.TestResult)
	 */
	public void addTestResult(TestResult testResult) {
		_wrappedTestSession.addTestResult(testResult);
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getIncomingMessages()
	 */
	public List<InMessage> getIncomingMessages() {
		return _wrappedTestSession.getIncomingMessages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getOutgoingMessages()
	 */
	public List<OutMessage> getOutgoingMessages() {
		return _wrappedTestSession.getOutgoingMessages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.cip4.tools.alces.TestSession#getTestResults()
	 */
	public List<TestResult> getTestResults() {
		return _wrappedTestSession.getTestResults();
	}

	public OutMessage getOutgoingMessage(InMessage message) {
		return _wrappedTestSession.getOutgoingMessage(message);
	}

	public InMessage getIncomingMessage(OutMessage message) {
		return _wrappedTestSession.getIncomingMessage(message);
	}

	public Message getInitiatingMessage() {
		return _wrappedTestSession.getInitiatingMessage();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void addListener(TestSessionListener listener) {
		_wrappedTestSession.addListener(listener);
	}

	public void removeListener(TestSessionListener listener) {
		_wrappedTestSession.removeListener(listener);
	}

	public boolean hasPassedAllTests() {
		return _wrappedTestSession.hasPassedAllTests();
	}
}
