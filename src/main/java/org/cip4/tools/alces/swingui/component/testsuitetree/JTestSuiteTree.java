package org.cip4.tools.alces.swingui.component.testsuitetree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * The TestSession tree object.
 */
public class JTestSuiteTree extends JTree {

    /**
     * Private custom constructor. Accepting a TestSuite for initializing.
     */
    private JTestSuiteTree(TreeNode treeNode) {
        super(treeNode);

    }

    /**
     * Create a new instance of the JTestSuiteTree object.
     * @return The new instance of the JTestSuiteTree object.
     */
    public static JTestSuiteTree newInstance() {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Test Suite");

        rootNode.add(new DefaultMutableTreeNode("Item 1"));
        rootNode.add(new DefaultMutableTreeNode("Item 2"));
        rootNode.add(new DefaultMutableTreeNode("Item 3"));

        // create and return object
        return new JTestSuiteTree(rootNode);
    }
}
