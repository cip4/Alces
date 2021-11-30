package org.cip4.tools.alces.service.testrunner.model;

import org.cip4.jdflib.core.JDFConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Model object of an incoming JMF Message.
 */
public class IncomingJmfMessage extends AbstractJmfMessage {

    private final List<OutgoingJmfMessage> outgoingJmfMessages;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param contentType The content type of the message.
     * @param jmfBody The jmf message body.
     */
    public IncomingJmfMessage(String contentType, String jmfBody) {
        super(contentType, jmfBody);

        this.outgoingJmfMessages = new ArrayList<>();
    }

    /**
     * Custom constructor. Accepting multiple params for initializing.
     * @param jmfBody The jmf message body.
     */
    public IncomingJmfMessage(String jmfBody) {
        this(JDFConstants.MIME_JMF, jmfBody);
    }

    public List<OutgoingJmfMessage> getOutgoingJmfMessages() {
        return outgoingJmfMessages;
    }
}
