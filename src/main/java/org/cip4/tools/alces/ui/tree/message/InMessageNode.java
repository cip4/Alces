package org.cip4.tools.alces.ui.tree.message;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.ui.tree.test.TestResultNode;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An <code>DefaultMutableTreeNode</code> implementation of an <code>InMessage</code>.
 */
public class InMessageNode extends AbstractMessageNode {

    protected static Logger log = LoggerFactory.getLogger(InMessageNode.class);
    
    /**
     * Creates an InMessage that is a TreeNode and wraps a non-Swing InMessage
     * implementation. The wrapped InMessage contains the logic, this class is simply
     * a wrapper allowing the InMessage to be displayed in a JTree. 
     * @param jmfMessage   The Message to wrap. This object may not be an instance of TreeNode.
     * @param treeModel the tree model this node belongs to
     */
    public InMessageNode(IncomingJmfMessage jmfMessage, DefaultTreeModel treeModel) {
        super(jmfMessage, treeModel);
    }
    
    /** 
     * Creates an InMessage that is a TreeNode and wraps a non-Swing InMessage
     * implementation. The wrapped InMessage contains the logic, this class is simply
     * a wrapper allowing the InMessage to be displayed in a JTree.
     * @param treeModel the tree model this node belongs to
     */
    public InMessageNode(String header, String body, boolean isSessionInitiator, DefaultTreeModel treeModel) {
        this(new IncomingJmfMessage(header, body, isSessionInitiator), treeModel);
    }
    
    public List<OutgoingJmfMessage> getOutMessages() {
        return ((IncomingJmfMessage) getJmfMessage()).getOutgoingJmfMessages();
    }
    
    public void addOutMessage(OutgoingJmfMessage message) {
        log.debug("Adding OutMessage to message/tree: " + message);
        final MutableTreeNode thisNode = this;

        final OutMessageNode messageNode = new OutMessageNode(message, getTreeModel());
            // Add TestResults to OutMessage            
            for (TestResult testResult : messageNode.getJmfMessage().getTestResults()) {
                if(!(testResult instanceof TreeNode && isNodeChild((TreeNode)testResult))) {
                    messageNode.add(new TestResultNode(testResult, getTreeModel()));
                    log.debug("Added TestResult to " + messageNode.getClass().getName() + ".");
                }
            }


        getOutMessages().add((OutgoingJmfMessage) messageNode.getJmfMessage());

        // Add OutMessage to tree
        // Update model using Swing's event-dispatching thread
        Runnable addOutMessage = () -> {
            log.debug("Inserting OutMessage as child to InMessage in tree model...");
            getTreeModel().insertNodeInto(messageNode, thisNode, thisNode.getChildCount());
            log.debug("Inserted OutMessage in tree model.");
        };
        log.debug("Queueing OutMessage for insertion as chld to InMessage in tree model...");
        SwingUtilities.invokeLater(addOutMessage);   
    }
}
