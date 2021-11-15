/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.swingui.tree.message;

import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;

/**
 * A <code>DefaultMutableTreeNode</code> implementation of a
 * <code>OutMessage</code>.
 */
public class OutMessageNode extends AbstractMessageNode {

    /**
     * Creates an OutMessage that is a TreeNode and wraps a non-Swing OutMessage
     * implementation. The wrapped OutMessage contains the logic, this class is simply
     * a wrapper allowing the OutMessage to be displayed in a JTree. 
     * @param message   The Message to wrap. This object may not be an instance of TreeNode.
     * @param treeModel the tree model this node belongs to
     */
	public OutMessageNode(OutgoingJmfMessage message, DefaultTreeModel treeModel) {
		super(message, treeModel);
	}

    /** 
     * Creates an OutMessage that is a TreeNode and wraps a non-Swing OutMessage
     * implementation. The wrapped OutMessage contains the logic, this class is simply
     * a wrapper allowing the OutMessage to be displayed in a JTree. 
     * @param treeModel the tree model this node belongs to
     */
	public OutMessageNode(String header, String body, boolean isSessionInitiator, DefaultTreeModel treeModel) {
		this(new OutgoingJmfMessage(header, body, isSessionInitiator), treeModel);
	}

	/**
	 * Returns a <code>List</code> containing all <code>InMessages</code>
	 * received as a result of this <code>OutMessage</code> being sent.
	 * 
	 * @return a <code>List</code> of <code>InMessages</code>
	 */
	public List<IncomingJmfMessage> getInMessages() {
		return ((OutgoingJmfMessage) getJmfMessage()).getIncomingJmfMessages();
	}


}
