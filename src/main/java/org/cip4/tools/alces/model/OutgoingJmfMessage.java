package org.cip4.tools.alces.model;


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
     * @param header The http header of the message.
     * @param body The message body.
     * @param isSessionInitiator flag if message is a session initiator.
     */
    public OutgoingJmfMessage(String contentType, String header, String body, boolean isSessionInitiator) {
        super(contentType, header, body, isSessionInitiator);

        this.incomingJmfMessages = new ArrayList<>();
    }

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param header The http header of the message.
     * @param body The message body.
     * @param isSessionInitiator flag if message is a session initiator.
     */
    public OutgoingJmfMessage(String header, String body, boolean isSessionInitiator) {
        this(JDFConstants.MIME_JMF, header, body, isSessionInitiator);
    }

    /**
     * Custom constructor. Creating a Outgoing JmfMessage form a jmf message body only.
     * @param jmfBody The JMF message body.
     */
    public OutgoingJmfMessage(String jmfBody) {
        this(JDFConstants.MIME_JMF, "Content-Type: " + JDFConstants.MIME_JMF, jmfBody, true);
    }

    public List<IncomingJmfMessage> getIncomingJmfMessages() {
        return incomingJmfMessages;
    }
}
