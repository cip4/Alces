package org.cip4.tools.alces.ui.component;

import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.cip4.tools.alces.service.testrunner.model.TestSessionsListener;
import org.cip4.tools.alces.util.JmfUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The test suite tree component.
 */
public class JTestSessionsTree extends JTree implements TestSessionsListener {

    private final static String RES_ROOT = "/org/cip4/tools/alces/ui/";

    private final JTestSessionsTree testSuiteTree;

    /**
     * The root node.
     */
    private final DefaultMutableTreeNode rootNode;

    /**
     * Private custom constructor. Accepting a TestSuite for initializing.
     */
    private JTestSessionsTree(DefaultMutableTreeNode treeNode) throws IOException {
        super(treeNode);

        this.rootNode = treeNode;
        this.testSuiteTree = this;

        setRootVisible(false);
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(getTreeCellRenderer());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    TreePath treePath = getClosestPathForLocation(e.getX(), e.getY());
                    setSelectionPath(treePath);

                    JPopupMenu popupMenu = new JPopupMenu();

                    // collapse all
                    popupMenu.add(new AbstractAction("Collapse All") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            for(int i = 0; i < testSuiteTree.getRowCount(); i ++) {
                                testSuiteTree.collapseRow(i);
                            }
                        }
                    });

                    // expand all
                    popupMenu.add(new AbstractAction("Expand All") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            for(int i = 0; i < testSuiteTree.getRowCount(); i ++) {
                                testSuiteTree.expandRow(i);
                            }
                        }
                    });

                    // show Popup Menu
                    popupMenu.show(testSuiteTree, e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Create a new instance of the JTestSessionsTree object.
     * @return The new instance of the JTestSessionsTree object.
     */
    public static JTestSessionsTree newInstance(List<TestSession> testSessions) throws IOException {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Test Suite");

        testSessions.forEach(testSession -> {
            rootNode.add(new DefaultMutableTreeNode(testSession));
        });

        // create and return object
        return new JTestSessionsTree(rootNode);
    }

    @Override
    public void handleTestSessionsUpdate(List<TestSession> testSessions) {

        // save node expansion state
        List<Object> expandedUserObjects = new ArrayList<>();

        for(int i = 0; i < getRowCount(); i ++) {
            if(isExpanded(i)) {
                TreePath treePath = getPathForRow(i);
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getPathComponent(treePath.getPathCount() - 1);
                expandedUserObjects.add(treeNode.getUserObject());
            }
        }

        // remove items
        rootNode.removeAllChildren();

        // build new node structure
        testSessions.forEach(testSession -> {

            // add test session node (if not present)
            DefaultMutableTreeNode testSessionTreeNode = new DefaultMutableTreeNode(testSession);
            rootNode.add(testSessionTreeNode);

            // add outgoing message
            testSession.getOutgoingJmfMessages().forEach(outgoingJmfMessage -> {
                DefaultMutableTreeNode outgoingJmfMessageNode = new DefaultMutableTreeNode(outgoingJmfMessage);
                testSessionTreeNode.add(outgoingJmfMessageNode);

                // add test results
                outgoingJmfMessage.getTestResults().forEach(testResult -> {
                    outgoingJmfMessageNode.add(new DefaultMutableTreeNode(testResult));
                });

                // add related incoming messages
                outgoingJmfMessage.getIncomingJmfMessages().forEach(incomingJmfMessage -> {
                    DefaultMutableTreeNode incomingJmfMessageNode = new DefaultMutableTreeNode(incomingJmfMessage);
                    outgoingJmfMessageNode.add(incomingJmfMessageNode);

                    // add test results
                    incomingJmfMessage.getTestResults().forEach(testResult -> {
                        incomingJmfMessageNode.add(new DefaultMutableTreeNode(testResult));
                    });
                });
            });
        });

        // reload tree
        ((DefaultTreeModel) getModel()).reload(rootNode);

        // restore node expansion state
        for(int i = 0; i < getRowCount(); i ++) {
            TreePath treePath = getPathForRow(i);
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getPathComponent(treePath.getPathCount() - 1);

            if(expandedUserObjects.contains(treeNode.getUserObject())) {
                expandPath(treePath);
            }
        }
    }

    /**
     * Returns a customized cell renderer.
     * @return The customized cell renderer.
     */
    private DefaultTreeCellRenderer getTreeCellRenderer() throws IOException {

        Icon iconMessageInPass = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "message_in_pass.gif").readAllBytes());
        Icon iconMessageInFail = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "message_in_fail.gif").readAllBytes());
        Icon iconMessageOutPass = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "message_out_pass.gif").readAllBytes());
        Icon iconMessageOutFail = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "message_out_fail.gif").readAllBytes());
        Icon iconTestPass = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "test_pass.gif").readAllBytes());
        Icon iconTestFail = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "test_fail.gif").readAllBytes());
        Icon iconTestIgnored = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "test_ignored.gif").readAllBytes());
        Icon iconSessionFail = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "session_fail.gif").readAllBytes());
        Icon iconSessionPass = new ImageIcon(getClass().getResourceAsStream(RES_ROOT + "session_pass.gif").readAllBytes());

        return new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

                if (userObject instanceof TestSession testSession) { // Node: TestSession

                    // set text/tooltip
                    setToolTipText(testSession.getTargetUrl());

                    if(testSession.getOutgoingJmfMessages().size() > 0) {
                        setText(createMessageLabel(testSession.getOutgoingJmfMessages().get(0)) + " - " +  testSession.getTargetUrl());
                    } else {
                        setText(testSession.getTargetUrl());
                    }

                    // set icon
                    if (testSession.hasPassedAllTests()) {
                        setIcon(iconSessionPass);
                    } else {
                        setIcon(iconSessionFail);
                    }

                } else if (userObject instanceof OutgoingJmfMessage outgoingJmfMessage) { // Node: Outgoing JMF Message

                    // set text/tooltip
                    setText(createMessageLabel(outgoingJmfMessage));
                    setToolTipText("Outgoing message");

                    // set icon
                    if (outgoingJmfMessage.hasPassedAllTests()) {
                        setIcon(iconMessageOutPass);
                    } else {
                        setIcon(iconMessageOutFail);
                    }

                } else if (userObject instanceof IncomingJmfMessage incomingJmfMessage) { // Node: Incoming JMF Message

                    // set text/tooltip
                    setText(createMessageLabel(incomingJmfMessage));
                    setToolTipText("Incoming message");

                    // set icon
                    if (incomingJmfMessage.hasPassedAllTests()) {
                        setIcon(iconMessageInPass);
                    } else {
                        setIcon(iconMessageInFail);
                    }

                } else if (userObject instanceof TestResult testResult) {

                    // set text
                    setText(testResult.getJmfTest().getDescription());

                    // set icons/tooltip
                    switch (testResult.getResult()) {
                        case PASSED -> {
                            setIcon(iconTestPass);
                            setToolTipText("Test passed");
                        }
                        case FAILED -> {
                            setIcon(iconTestFail);
                            setToolTipText("Test failed");
                        }
                        case IGNORED -> {
                            setIcon(iconTestIgnored);
                            setToolTipText("Test ignored");
                        }
                        default -> {
                        }
                    }
                }

                return this;
            }
        };
    }

    /**
     * Helper method to create the label's text for a JMF message.
     * @param abstractJmfMessage The JMF message.
     * @return The messages label text as String.
     */
    private String createMessageLabel(AbstractJmfMessage abstractJmfMessage) {
        StringBuilder nodeLabel = new StringBuilder();

        final JDFJMF jmf = JmfUtil.getBodyAsJMF(abstractJmfMessage);

        if (jmf != null) {
            final NodeList nodes = jmf.getJMFRoot().getChildNodes();
            for (int i = 0, imax = nodes.getLength(); i < imax; i++) {
                final Node node = nodes.item(i);

                if (node instanceof final JDFElement msg) {
                    nodeLabel.append(msg.getTagName());
                    nodeLabel.append(msg.getAttribute("Type"));
                    nodeLabel.append(" ");
                }
            }
        } else {
            nodeLabel.append(abstractJmfMessage.getContentType());
        }

        return nodeLabel.toString();
    }
}
