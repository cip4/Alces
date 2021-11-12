package org.cip4.tools.alces.swingui.renderer;

import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class AlcesTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Logger log = LoggerFactory.getLogger(AlcesTreeCellRenderer.class);

    private final Icon messageInPassIcon;
    private final Icon messageInFailIcon;
    private final Icon messageOutPassIcon;
    private final Icon messageOutFailIcon;
    private final Icon testPassIcon;
    private final Icon testFailIcon;
    private final Icon testIgnoredIcon;
    private final Icon sessionFailIcon;
    private final Icon sessionPassIcon;

    private final TestSession _testsession = null;

    public AlcesTreeCellRenderer() {
        messageInPassIcon = createIcon("/org/cip4/tools/alces/icons/message_in_pass.gif");
        messageInFailIcon = createIcon("/org/cip4/tools/alces/icons/message_in_fail.gif");
        messageOutPassIcon = createIcon("/org/cip4/tools/alces/icons/message_out_pass.gif");
        messageOutFailIcon = createIcon("/org/cip4/tools/alces/icons/message_out_fail.gif");
        testPassIcon = createIcon("/org/cip4/tools/alces/icons/test_pass.gif");
        testFailIcon = createIcon("/org/cip4/tools/alces/icons/test_fail.gif");
        testIgnoredIcon = createIcon("/org/cip4/tools/alces/icons/test_ignored.gif");
        sessionFailIcon = createIcon("/org/cip4/tools/alces/icons/session_fail.gif");
        sessionPassIcon = createIcon("/org/cip4/tools/alces/icons/session_pass.gif");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (log.isDebugEnabled()) {
            log.debug("Rendering tree cell " + value.getClass() + " - " + value + "...");
        }
        if (value instanceof OutgoingJmfMessage) {
            if (((OutgoingJmfMessage) value).hasPassedAllTests()) {
                setIcon(messageOutPassIcon);
                setToolTipText("Outgoing message");
            } else {
                setIcon(messageOutFailIcon);
                setToolTipText("Outgoing message");
            }
        } else if (value instanceof IncomingJmfMessage) {
            if (((IncomingJmfMessage) value).hasPassedAllTests()) {
                setIcon(messageInPassIcon);
                setToolTipText("Incoming message");
            } else {
                setIcon(messageInFailIcon);
                setToolTipText("Incoming message");
            }
        } else if (value instanceof TestSession) {
            if (((TestSession) value).hasPassedAllTests()) {
                setIcon(sessionPassIcon);
            } else {
                setIcon(sessionFailIcon);
            }
        } else if (value instanceof TestResult) {
            TestResult result = (TestResult) value;
            switch (result.getResult()) {
                case PASSED:
                    setIcon(testPassIcon);
                    setToolTipText("Test passed");
                    break;
                case FAILED:
                    setIcon(testFailIcon);
                    setToolTipText("Test failed");
                    break;
                case IGNORED:
                    setIcon(testIgnoredIcon);
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

    /**
     * Loads an icon.
     */
    private ImageIcon createIcon(String path) {
        ImageIcon icon = null;
        final java.net.URL imgURL = this.getClass().getResource(path);
        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
        } else {
            log.warn("Could not load icon from path: " + path);
        }
        return icon;
    }
}
