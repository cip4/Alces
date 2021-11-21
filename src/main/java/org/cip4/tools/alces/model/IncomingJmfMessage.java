package org.cip4.tools.alces.model;

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
     * @param header The http header of the message.
     * @param body The message body.
     * @param isSessionInitiator flag if message is a session initiator.
     */
    public IncomingJmfMessage(String contentType, String header, String body, boolean isSessionInitiator) {
        super(contentType, header, body, isSessionInitiator);

        this.outgoingJmfMessages = new ArrayList<>();
    }

    /**
     * Custom constructor. Accepting multiple params for initializing.
     *
     * @param header The http header of the message.
     * @param body The message body.
     * @param isSessionInitiator flag if message is a session initiator.
     */
    public IncomingJmfMessage(String header, String body, boolean isSessionInitiator) {
        this(JDFConstants.MIME_JMF, header, body, isSessionInitiator);
    }

    public List<OutgoingJmfMessage> getOutgoingJmfMessages() {
        return outgoingJmfMessages;
    }
}
