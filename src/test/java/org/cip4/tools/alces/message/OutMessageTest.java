/*
 * Created on Apr 27, 2005
 */
package org.cip4.tools.alces.message;


import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class OutMessageTest {

    @Test
    public void testTextBody() {
        String header = "header";
        String body = "body";
        AbstractJmfMessage m = new OutgoingJmfMessage(header, body, true);

        Assertions.assertEquals(m.getHeader(), header);
        Assertions.assertEquals(m.getBody(), body);
    }

    @Test
    public void testJMFBody() {
        String header = "header";
        String body = "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-04-27T17:32:13-04:00' Version='1.2' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Query ID='Link37533767_000000' Type='KnownMessages' xsi:type='QueryKnownMessages'/></JMF>";
        AbstractJmfMessage m = new OutgoingJmfMessage(header, body, true);

        System.out.println("String: " + m.getBody());

        Assertions.assertEquals(m.getHeader(), header);
        Assertions.assertEquals(m.getBody(), body);
    }

    @Test
    public void testJDFBody() {
        String header = "header";
        String body = "<?xml version='1.0' encoding='UTF-8'?><JDF />";
        AbstractJmfMessage m = new OutgoingJmfMessage(header, body, true);

        System.out.println("String: " + m.getBody());

        Assertions.assertEquals(m.getHeader(), header);
        Assertions.assertEquals(m.getBody(), body);

    }
}
