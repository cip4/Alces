/*
 * Created on Apr 28, 2005
 */
package org.cip4.tools.alces.message;

import java.util.List;
import java.util.Vector;

/**
 * An outgoing message sent by Alces.
 * @author Claes Buckwalter
 */
public class OutMessageImpl extends AbstractMessage implements OutMessage {

    protected List<InMessage> _inMessages = null;
    protected boolean _isSessionInitiator = false;
    
    public OutMessageImpl(String contentType, String header, String body, boolean isSessionInitiator) {
        super(contentType, header, body, isSessionInitiator);
        _inMessages = new Vector<InMessage>();
    }    
    
    public OutMessageImpl(String header, String body, boolean isSessionInitiator) {
        super(header, body, isSessionInitiator);        
        _inMessages = new Vector<InMessage>();
    }

    /**
     * Returns a <code>List</code> containing all <code>InMessages</code> received 
     * as a result of this <code>OutMessage</code> being sent.
     * @return a <code>List</code> of <code>InMessages</code>
     */
    public List<InMessage> getInMessages() {
        return _inMessages;
    }
    
    public void addInMessage(InMessage message) {
        _inMessages.add(message);
    }
}
