/*
 * Created on May 5, 2005
 */
package org.cip4.tools.alces.test.tests;

import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.jmftest.JDFContentTest;
import org.cip4.tools.alces.service.testrunner.tests.Test;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JDFContentTestTest {

    @org.junit.jupiter.api.Test
    public void testInvalidJMF() {
        AbstractJmfMessage msg = new IncomingJmfMessage("text/xml", null, "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-05-05T10:49:30+02:00' Version='1.1' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Signal ID='Link82970634_000011' Type='QueueStatus' refID='ALCESac0b51bd20dbf85a'><Notification AgentName='CIP4 JDF Writer Java' AgentVersion='1.2.42 alpha' Class='Event' TimeStamp='2005-05-05T10:49:30+02:00'><Comment>Status Change: Waiting -&gt; Closed</Comment><Comment>QueueStatusEvent[Status: Closed;  Class: JDFAutoNotification.EnumClass[Event=1];  Source: org.cip4.tools.impl.queue.MemoryQueue@e4d5ba;  Time stamp: 1115282970510]</Comment></Notification><Queue DeviceID='Elk' QueueSize='0' Status='Closed'/></Signal></JMF>", true);
        Assertions.assertNotEquals(JDFConstants.JMF_CONTENT_TYPE, msg.getContentType());
        Test test = new JDFContentTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testValidJMF() {
        AbstractJmfMessage msg = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, "Protocol: HTTP/1.1 content-type: application/vnd.cip4-jmf+xml; charset=UTF-8", "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-05-05T10:49:30+02:00' Version='1.1' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Signal ID='Link82970634_000011' Type='QueueStatus' refID='ALCESac0b51bd20dbf85a'><Notification AgentName='CIP4 JDF Writer Java' AgentVersion='1.2.42 alpha' Class='Event' TimeStamp='2005-05-05T10:49:30+02:00'><Comment>Status Change: Waiting -&gt; Closed</Comment><Comment>QueueStatusEvent[Status: Closed;  Class: JDFAutoNotification.EnumClass[Event=1];  Source: org.cip4.tools.impl.queue.MemoryQueue@e4d5ba;  Time stamp: 1115282970510]</Comment></Notification><Queue DeviceID='Elk' QueueSize='0' Status='Closed'/></Signal></JMF>", true);
        Assertions.assertEquals(JDFConstants.JMF_CONTENT_TYPE, msg.getContentType());
        Test test = new JDFContentTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testValidJMF2() {
        AbstractJmfMessage msg = new OutgoingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, "Content-Type: application/vnd.cip4-jmf+xml;charset=UTF-8 Transfer-Encoding: chunked Date: Wed, 20 Jul 2005 20:26:05 GMT Server: Apache-Coyote/1.1", "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-05-05T10:49:30+02:00' Version='1.1' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Signal ID='Link82970634_000011' Type='QueueStatus' refID='ALCESac0b51bd20dbf85a'><Notification AgentName='CIP4 JDF Writer Java' AgentVersion='1.2.42 alpha' Class='Event' TimeStamp='2005-05-05T10:49:30+02:00'><Comment>Status Change: Waiting -&gt; Closed</Comment><Comment>QueueStatusEvent[Status: Closed;  Class: JDFAutoNotification.EnumClass[Event=1];  Source: org.cip4.tools.impl.queue.MemoryQueue@e4d5ba;  Time stamp: 1115282970510]</Comment></Notification><Queue DeviceID='Elk' QueueSize='0' Status='Closed'/></Signal></JMF>", true);
        Test test = new JDFContentTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.getResult() == TestResult.Result.PASSED);
    }


    @org.junit.jupiter.api.Test
    public void testEmptyBody() {
        AbstractJmfMessage msg = new IncomingJmfMessage(null, "", true);
        Test test = new JDFContentTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testEmptyHeaderEmptyBody() {
        AbstractJmfMessage msg = new IncomingJmfMessage(null, null, true);
        Test test = new JDFContentTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

}
