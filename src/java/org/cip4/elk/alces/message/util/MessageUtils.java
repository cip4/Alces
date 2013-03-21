/*
 * Created on Jun 10, 2005
 */
package org.cip4.elk.alces.message.util;

import org.cip4.elk.alces.jmf.JMFMessageFactory;
import org.cip4.elk.alces.message.OutMessageImpl;
import org.cip4.jdflib.jmf.JDFJMF;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class MessageUtils {

    /**
     * Returns an instance of a <code>Message</code> generated from the specified 
     * message template.
     * 
     * TODO Cache loaded messages
     * TODO Set message header
     * 
     * @param messageTemplate   the name of the message's template
     * @param initiatingMessage true if the message created will initiate a test session
     * @return a message generated from the specified template
     */
    public static OutMessageImpl createMessage(String messageTemplate, boolean initiatingMessage) {
        String header = null;
        String body = null;
        header = "Content-type: application/vnd.cip4-jmf+xml";
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF(messageTemplate); 
        body = jmf.getOwnerDocument_KElement().write2String(2);        
        return new OutMessageImpl(header, body, initiatingMessage);
    }        
}
