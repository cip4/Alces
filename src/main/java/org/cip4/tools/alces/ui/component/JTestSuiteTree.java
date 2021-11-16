package org.cip4.tools.alces.ui.component;

import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.cip4.tools.alces.service.testrunner.model.TestSuite;
import org.cip4.tools.alces.service.testrunner.model.TestSuiteListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.IOException;

/**
 * The TestSession tree object.
 */
public class JTestSuiteTree extends JTree implements TestSuiteListener {

    private final static String RES_ROOT = "/org/cip4/tools/alces/icons/";

    /**
     * The root node.
     */
    private final DefaultMutableTreeNode rootNode;

    /**
     * Private custom constructor. Accepting a TestSuite for initializing.
     */
    private JTestSuiteTree(DefaultMutableTreeNode treeNode) throws IOException {
        super(treeNode);
        this.rootNode = treeNode;

        setRootVisible(false);
        setShowsRootHandles(true);
        // tree.addTreeSelectionListener(this);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // tree.setCellRenderer(new AlcesTreeCellRenderer());
        setCellRenderer(getTreeCellRenderer());
    }

    /**
     * Create a new instance of the JTestSuiteTree object.
     * @return The new instance of the JTestSuiteTree object.
     */
    public static JTestSuiteTree newInstance(TestSuite testSuite) throws IOException {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Test Suite");

        testSuite.getTestSessions().forEach(testSession -> {
            rootNode.add(new DefaultMutableTreeNode(testSession.toString()));
        });

        // create and return object
        return new JTestSuiteTree(rootNode);
    }

    @Override
    public void handleTestSuiteUpdate(TestSuite testSuite) {
        rootNode.removeAllChildren();

        testSuite.getTestSessions().forEach(testSession -> {
            rootNode.add(new DefaultMutableTreeNode(testSession.toString()));
        });

        ((DefaultTreeModel) getModel()).reload();
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

                if (value instanceof OutgoingJmfMessage) {
                    if (((OutgoingJmfMessage) value).hasPassedAllTests()) {
                        setIcon(iconMessageOutPass);
                        setToolTipText("Outgoing message");
                    } else {
                        setIcon(iconMessageOutFail);
                        setToolTipText("Outgoing message");
                    }
                } else if (value instanceof IncomingJmfMessage) {
                    if (((IncomingJmfMessage) value).hasPassedAllTests()) {
                        setIcon(iconMessageInPass);
                        setToolTipText("Incoming message");
                    } else {
                        setIcon(iconMessageInFail);
                        setToolTipText("Incoming message");
                    }
                } else if (value instanceof TestSession) {
                    if (((TestSession) value).hasPassedAllTests()) {
                        setIcon(iconSessionPass);
                    } else {
                        setIcon(iconSessionFail);
                    }
                } else if (value instanceof TestResult) {
                    TestResult result = (TestResult) value;
                    switch (result.getResult()) {
                        case PASSED:
                            setIcon(iconTestPass);
                            setToolTipText("Test passed");
                            break;
                        case FAILED:
                            setIcon(iconTestFail);
                            setToolTipText("Test failed");
                            break;
                        case IGNORED:
                            setIcon(iconTestIgnored);
                            setToolTipText("Test ignored");
                            break;
                        default:
                    }
                } else {
                    setIcon(null);
                    setToolTipText("No tooltip");
                }

                return this;
            }
        };
    }
}
