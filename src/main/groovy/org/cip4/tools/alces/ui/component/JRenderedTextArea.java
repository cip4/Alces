package org.cip4.tools.alces.ui.component;

import org.cip4.jdflib.core.JDFConstants;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;

import javax.swing.*;

/**
 * The rendered text area component.
 */
public class JRenderedTextArea extends JTextArea {

    /**
     * Default constructor.
     */
    public JRenderedTextArea() {
        this(null);
    }

    /**
     * Custom constructor. Accepting an user object for initializing.
     * @param userObject The user object.
     */
    public JRenderedTextArea(Object userObject) {
        setEditable(false);
        setLineWrap(false);
        setWrapStyleWord(false);

        // analyze input
        if (userObject instanceof AbstractJmfMessage abstractJmfMessage) { // jmf message
            processOutput(abstractJmfMessage);
        } else {
            setText("bla");
        }
    }

    /**
     * Set the user object
     */
    public void setUserObject(Object userObject) {

        // analyze input
        if (userObject instanceof AbstractJmfMessage abstractJmfMessage) { // jmf message
            processOutput(abstractJmfMessage);
        }

    }

    private void processOutput(AbstractJmfMessage abstractJmfMessage) {

        if (JDFConstants.MIME_JMF.equals(abstractJmfMessage.getContentType()) || JDFConstants.MIME_JDF.equals(abstractJmfMessage.getContentType())) {
            setText(abstractJmfMessage.getBody());
        } else {
            setText("Error: unsupported content type");
        }
    }
}
