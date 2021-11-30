package org.cip4.tools.alces.service.testrunner.model;

import org.cip4.jdflib.core.JDFConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Model object of an outgoing JMF Message.
 */
public class OutgoingJmfMessage extends AbstractJmfMessage {

    private final List<IncomingJmfMessage> incomingJmfMessages;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param contentType The content type of the message.
     * @param body The message body.
     */
    public OutgoingJmfMessage(String contentType, String body) {
        super(contentType, body);

        this.incomingJmfMessages = new ArrayList<>();
    }

    /**
     * Custom constructor. Creating a Outgoing JmfMessage form a jmf message body only.
     * @param jmfBody The JMF message body.
     */
    public OutgoingJmfMessage(String jmfBody) {
        this(JDFConstants.MIME_JMF, jmfBody);
    }

    public List<IncomingJmfMessage> getIncomingJmfMessages() {
        return incomingJmfMessages;
    }
}
