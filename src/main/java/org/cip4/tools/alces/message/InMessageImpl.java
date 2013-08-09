/*
 * Created on Apr 28, 2005
 */
package org.cip4.tools.alces.message;

import java.util.List;
import java.util.Vector;

/**
 * An incoming message received by Alces.
 * 
 * @author Claes Buckwalter
 */
public class InMessageImpl extends AbstractMessage implements InMessage {

    protected List<OutMessage> _outMessages = null;
    
    public InMessageImpl(String contentType, String header, String body, boolean isSessionInitiator) {
        super(contentType, header, body, isSessionInitiator);
        _outMessages = new Vector<OutMessage>();
    }    
    
    public InMessageImpl(String header, String body, boolean isSessionInitiator) {
        super(header, body, isSessionInitiator);
        _outMessages = new Vector<OutMessage>();
    }
        
    public List<OutMessage> getOutMessages() {
        return _outMessages;
    }
    
    public void addOutMessage(OutMessage message) {
        _outMessages.add(message);
    }
}
