package org.cip4.tools.alces.test.tests;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.jmftest.JmfTest;
import org.cip4.tools.alces.service.testrunner.jmftest.ReturnCodeJmfTest;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class ReturnCodeTestTest extends AlcesTestCase {

    @org.junit.jupiter.api.Test
    public void testReturnCode() throws IOException {
        String jmf = getTestFileAsString("ResponseKnownMessages.jmf");
        IncomingJmfMessage msg = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        JmfTest t = new ReturnCodeJmfTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertTrue(tr.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testReturnCode_Error() throws IOException {
        String jmf = getTestFileAsString("ResponseKnownMessages-ReturnCode1.jmf");
        IncomingJmfMessage msg = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        JmfTest t = new ReturnCodeJmfTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertFalse(tr.getResult() == TestResult.Result.PASSED);
    }

    @org.junit.jupiter.api.Test
    public void testSignal() throws IOException {
        String jmf = getTestFileAsString("Signal.jmf");
        IncomingJmfMessage msg = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        JmfTest t = new ReturnCodeJmfTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertTrue(tr.getResult() == TestResult.Result.PASSED);
    }
}
