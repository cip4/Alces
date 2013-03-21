/*
 * Created on May 4, 2005
 */
package org.cip4.elk.alces.message;

import java.util.List;

/**
 * Represents an incoming message received by Alces. An incoming message could
 * for example be a <em>ReturnQueueEntry</em> JMF message.
 * 
 * @author Claes Buckwalter
 */
public interface InMessage extends Message {

    /**
     * Returns a <code>List</code> containing all <code>OutMessage</code> s
     * sent as a result of this <code>InMessage</code> being received.
     * 
     * @return a <code>List</code> of <code>OutMessage</code> s
     */
    public List<OutMessage> getOutMessages();

    /**
     * Adds a message to the list of <code>OutMessage</code> s sent as a
     * result of this <code>InMessage</code> being received.
     * 
     * @param message
     */
    public void addOutMessage(OutMessage message);
}
