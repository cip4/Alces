/**
 * @author Niels Boeger
 * Created on Nov 07, 2006
 */
package org.cip4.tools.alces.test.tests;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class ReturnCodeTestTest extends AlcesTestCase {

    @org.junit.jupiter.api.Test
    public void testReturnCode() throws IOException {
        String jmf = getTestFileAsString("ResponseKnownMessages.jmf");
        InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        Test t = new ReturnCodeTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertTrue(tr.isPassed());
    }

    @org.junit.jupiter.api.Test
    public void testReturnCode_Error() throws IOException {
        String jmf = getTestFileAsString("ResponseKnownMessages-ReturnCode1.jmf");
        InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        Test t = new ReturnCodeTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertFalse(tr.isPassed());
    }

    @org.junit.jupiter.api.Test
    public void testSignal() throws IOException {
        String jmf = getTestFileAsString("Signal.jmf");
        InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
        Test t = new ReturnCodeTest();
        TestResult tr = t.runTest(msg);
        Assertions.assertNotNull(tr);
        Assertions.assertTrue(tr.isPassed());
    }
}
