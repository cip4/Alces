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
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.swingui.tree.test.TestResultNode;
import org.cip4.tools.alces.test.TestResult;


/**
 * An <code>DefaultMutableTreeNode</code> implementation of an <code>InMessage</code>.
 * 
 * @see javax.swing.tree.DefaultMutableTreeNode
 * @see org.cip4.tools.alces.message.InMessage
 * @author Claes Buckwalter
 */
public class InMessageNode extends AbstractMessageNode implements
        InMessage {
    
    private List<OutMessage> _outMessages = null;    
    
    /** 
     * Creates an InMessage that is a TreeNode and wraps a non-Swing InMessage
     * implementation. The wrapped InMessage contains the logic, this class is simply
     * a wrapper allowing the InMessage to be displayed in a JTree. 
     * @param message   The Message to wrap. This object may not be an instance of TreeNode.
     * @param treeModel the tree model this node belongs to
     */
    public InMessageNode(InMessage message, DefaultTreeModel treeModel) {
        super(message, treeModel);        
        _outMessages = new Vector<OutMessage>();
    }
    
    /** 
     * Creates an InMessage that is a TreeNode and wraps a non-Swing InMessage
     * implementation. The wrapped InMessage contains the logic, this class is simply
     * a wrapper allowing the InMessage to be displayed in a JTree.
     * @param treeModel the tree model this node belongs to
     */
    public InMessageNode(String header, String body, boolean isSessionInitiator, DefaultTreeModel treeModel) {
        this(new InMessageImpl(header, body, isSessionInitiator), treeModel);
    }
    
    public List<OutMessage> getOutMessages() {
        return _outMessages;
    }
    
    public void addOutMessage(OutMessage message) {
        LOGGER.debug("Adding OutMessage to message/tree: " + message);
        final MutableTreeNode thisNode = this;
        final OutMessageNode messageNode;
        if (message instanceof OutMessageNode ) {
            messageNode = (OutMessageNode) message;
        } else {
            messageNode = new OutMessageNode(message, _treeModel);
            // Add TestResults to OutMessage            
            for (TestResult testResult : messageNode.getTestResults()) {                
                if(!(testResult instanceof TreeNode && isNodeChild((TreeNode)testResult))) {
                    messageNode.add(new TestResultNode(testResult, _treeModel));           
                    LOGGER.debug("Added TestResult to " + messageNode.getClass().getName() + ".");
                }
            }
        }
        _outMessages.add(messageNode);
        // Add OutMessage to tree
        // Update model using Swing's event-dispatching thread
        Runnable addOutMessage = new Runnable() {
            public void run() {
                LOGGER.debug("Inserting OutMessage as child to InMessage in tree model...");
                _treeModel.insertNodeInto(messageNode, thisNode, thisNode.getChildCount());                
                LOGGER.debug("Inserted OutMessage in tree model.");
            }
        };
        LOGGER.debug("Queueing OutMessage for insertion as chld to InMessage in tree model...");
        SwingUtilities.invokeLater(addOutMessage);   
    }

	public boolean hasPassedAllTests() {
		return _wrappedMessage.hasPassedAllTests();		
	}
}
