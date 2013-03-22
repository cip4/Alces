/*
 * Created on May 4, 2005
 */
package org.cip4.tools.alces.message;

import java.util.List;

/**
 * Represents an outgoing message sent by Alces.
 * 
 * @author Claes Buckwalter
 */
public interface OutMessage extends Message {

    /**
     * Returns a <code>List</code> containing all <code>InMessage</code>s received 
     * as a result of this <code>OutMessage</code> being sent.
     * @return a <code>List</code> of <code>InMessage</code>s
     */
    public List<InMessage> getInMessages();
    
    /**
     * Adds a message to the list of <code>InMessage</code>s received 
     * as a result of this <code>OutMessage</code> being sent.
     * @param message
     */
    public void addInMessage(InMessage message);
}
