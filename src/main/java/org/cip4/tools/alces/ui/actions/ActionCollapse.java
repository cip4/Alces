package org.cip4.tools.alces.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * An action to collapse a messages in the message pane.
 * 
 * @author Alex Khilov
 * @since 0.9.9.3
 */
public class ActionCollapse extends AbstractAction {
    private JTree tree;
    private TreePath path;
    
    
    public ActionCollapse(final JTree tree, final TreePath path) {
        this.tree = tree;
        this.path = path;
    }

    public void actionPerformed(ActionEvent e) {
        if (tree.isExpanded(path)) {
            tree.collapsePath(path);
        }
    }
}
