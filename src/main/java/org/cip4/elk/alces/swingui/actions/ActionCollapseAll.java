package org.cip4.elk.alces.swingui.actions;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * An action to collapse all messages listed in the message pane.
 * 
 * @author Alex Khilov
 * @since 0.9.9.3
 */
public class ActionCollapseAll extends AbstractAction {
	private JTree tree;

	public ActionCollapseAll(final JTree tree) {
		this.tree = tree;
	}

	public void actionPerformed(ActionEvent e) {
		expandAll(tree, false);
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.reload();
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
}
