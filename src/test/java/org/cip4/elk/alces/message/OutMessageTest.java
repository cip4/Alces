/*
 * Created on Apr 27, 2005
 */
package org.cip4.elk.alces.message;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.message.OutMessageImpl;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class OutMessageTest extends AlcesTestCase {

    public void testTextBody() {
        String header = "header";
        String body = "body";
        Message m = new OutMessageImpl(header, body, true);
        
        assertEquals(m.getHeader(), header);
        assertEquals(m.getBody(), body);
        
        assertNull(m.getBodyAsJDOM());
        assertNull(m.getBodyAsJDF());
        assertNull(m.getBodyAsJMF());        
    }
    
    public void testJMFBody() {
        String header = "header";
        String body = "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-04-27T17:32:13-04:00' Version='1.2' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Query ID='Link37533767_000000' Type='KnownMessages' xsi:type='QueryKnownMessages'/></JMF>";
        Message m = new OutMessageImpl(header, body, true);
        
        System.out.println("String: " + m.getBody());
        System.out.println("JDOM: " + m.getBodyAsJDOM());
        System.out.println("JDF: " + m.getBodyAsJDF());
        System.out.println("JMF: " + m.getBodyAsJMF());
        
        assertEquals(m.getHeader(), header);
        assertEquals(m.getBody(), body);       
        
        assertNotNull(m.getBodyAsJDOM());
        assertNull(m.getBodyAsJDF());
        assertNotNull(m.getBodyAsJMF());
    }
    
    public void testJDFBody() {
        String header = "header";
        String body = "<?xml version='1.0' encoding='UTF-8'?><JDF />";
        Message m = new OutMessageImpl(header, body, true);
        
        System.out.println("String: " + m.getBody());
        System.out.println("JDOM: " + m.getBodyAsJDOM());
        System.out.println("JDF: " + m.getBodyAsJDF());
        System.out.println("JMF: " + m.getBodyAsJMF());
        
        assertEquals(m.getHeader(), header);
        assertEquals(m.getBody(), body);       
        
        assertNotNull(m.getBodyAsJDOM());
        assertNotNull(m.getBodyAsJDF());
        assertNull(m.getBodyAsJMF());
    }
}
