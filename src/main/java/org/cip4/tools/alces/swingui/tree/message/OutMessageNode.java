/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.message;

import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.swingui.tree.test.TestResultNode;
import org.cip4.tools.alces.test.TestResult;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a
 * <code>OutMessage</code>.
 * 
 * @see javax.swing.tree.DefaultMutableTreeNode
 * @see org.cip4.tools.alces.message.OutMessage
 * @author Claes Buckwalter
 */
public class OutMessageNode extends AbstractMessageNode implements OutMessage {

	private List<InMessage> _inMessages = null;

    /** 
     * Creates an OutMessage that is a TreeNode and wraps a non-Swing OutMessage
     * implementation. The wrapped OutMessage contains the logic, this class is simply
     * a wrapper allowing the OutMessage to be displayed in a JTree. 
     * @param message   The Message to wrap. This object may not be an instance of TreeNode.
     * @param treeModel the tree model this node belongs to
     */
	public OutMessageNode(OutMessage message, DefaultTreeModel treeModel) {
		super(message, treeModel);
		_inMessages = new Vector<InMessage>();
	}

    /** 
     * Creates an OutMessage that is a TreeNode and wraps a non-Swing OutMessage
     * implementation. The wrapped OutMessage contains the logic, this class is simply
     * a wrapper allowing the OutMessage to be displayed in a JTree. 
     * @param treeModel the tree model this node belongs to
     */
	public OutMessageNode(String header, String body, boolean isSessionInitiator, DefaultTreeModel treeModel) {
		this(new OutMessageImpl(header, body, isSessionInitiator), treeModel);
	}

	/**
	 * Returns a <code>List</code> containing all <code>InMessages</code>
	 * received as a result of this <code>OutMessage</code> being sent.
	 * 
	 * @return a <code>List</code> of <code>InMessages</code>
	 */
	public List<InMessage> getInMessages() {
		return _inMessages;
	}

	public void addInMessage(InMessage message) {
	    log.debug("Adding InMessage to message/tree: " + message);
        final MutableTreeNode thisNode = this;
        final InMessageNode messageNode;
		if (message instanceof InMessageNode) {
            messageNode = (InMessageNode) message;
		} else {
			messageNode = new InMessageNode(message, _treeModel);		
			// Add TestResults to InMessage            
			for (TestResult testResult : messageNode.getTestResults()) {				
				if (!(testResult instanceof TreeNode && isNodeChild((TreeNode) testResult))) {
					messageNode.add(new TestResultNode(testResult, _treeModel));
					log.debug("Added TestResult to "
							+ messageNode.getClass().getName() + ".");
				}
			}
		}
		_inMessages.add(messageNode);
        // Add InMessage to tree
        // Update model using Swing's event-dispatching thread
        Runnable addInMessage = new Runnable() {
            public void run() {
                log.debug("Inserting InMessage as child to OutMessage in tree model...");
                _treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
                log.debug("Inserted InMessage in tree model.");                
            }
        };
        log.debug("Queueing InMessage for insertion as child to OutMessage in tree model...");
        SwingUtilities.invokeLater(addInMessage);
	}

	public boolean hasPassedAllTests() {
		return _wrappedMessage.hasPassedAllTests();
	}
}
