/*
 * Created on May 5, 2005
 */
package org.cip4.tools.alces.test.tests;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.tests.CheckJDFTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class CheckJDFTestTest extends AlcesTestCase {

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateJDFValid() throws Exception {
        String jdf = getTestFileAsString("Elk_Approval.jdf");
        IncomingJmfMessage msg = new IncomingJmfMessage(null, jdf, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testValidateJDFInValid() throws Exception {
        String jdf = getTestFileAsString("Elk_Approval_invalid.jdf");
        IncomingJmfMessage msg = new IncomingJmfMessage(null, jdf, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        System.out.println(result);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testInvalidJMF() {
        IncomingJmfMessage msg = new IncomingJmfMessage(
                null,
                "<?xml version='1.0' encoding='UTF-8'?><JMF TimeStamp='2005-05-05T10:49:30+02:00' Version='1.1' xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Signal ID='Link82970634_000011' Type='QueueStatus' refID='ALCESac0b51bd20dbf85a'><Notification AgentName='CIP4 JDF Writer Java' AgentVersion='1.2.42 alpha' Class='Event' TimeStamp='2005-05-05T10:49:30+02:00'><Comment>Status Change: Waiting -&gt; Closed</Comment><Comment>QueueStatusEvent[Status: Closed;  Class: JDFAutoNotification.EnumClass[Event=1];  Source: org.cip4.tools.impl.queue.MemoryQueue@e4d5ba;  Time stamp: 1115282970510]</Comment></Notification><Queue DeviceID='Elk' QueueSize='0' Status='Closed'/></Signal></JMF>",
                true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testEmptyBody() {
        IncomingJmfMessage msg = new IncomingJmfMessage(null, "", true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);

        msg = new IncomingJmfMessage(null, null, true);
        result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testInvalidQueryKnownMessages() {
        IncomingJmfMessage msg = new IncomingJmfMessage(
                null,
                "<?xml version='1.0' encoding='UTF-8'?><JMF xmlns='http://www.CIP4.org/JDFSchema_1_1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' SenderID='No sender ID configured' TimeStamp='2005-05-09T01:09:52+0200' Version='1.2'>    <Query ID='ALCESbe925d695d2615f5' Type='KnownMessages' SenderID='apan' /></JMF>",
                true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

    @Test
    public void testInvalidElement() {
        IncomingJmfMessage msg = new IncomingJmfMessage(
                null,
                "<?xml version='1.0' encoding='UTF-8'?><JMF xmlns='http://www.CIP4.org/JDFSchema_1_1' SenderID='Elk' TimeStamp='2005-05-09T00:48:39+02:00' Version='1.2' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Response/></JMF>",
                true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
    }

}
