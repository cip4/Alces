/**
 * @author Niels Boeger
 * Created on Nov 07, 2006
 */
package org.cip4.elk.alces.test.tests;

import java.io.IOException;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.InMessage;
import org.cip4.elk.alces.message.InMessageImpl;
import org.cip4.elk.alces.test.TestResult;
import org.cip4.elk.alces.util.JDFConstants;

public class ReturnCodeTestTest extends AlcesTestCase {

	public void testReturnCode() throws IOException {
		String jmf = getTestFileAsString("ResponseKnownMessages.jmf");
		InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
		Test t = new ReturnCodeTest();
		TestResult tr = t.runTest(msg);
		assertNotNull(tr);
		assertTrue(tr.isPassed());
	}

	public void testReturnCode_Error() throws IOException {
		String jmf = getTestFileAsString("ResponseKnownMessages-ReturnCode1.jmf");
		InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
		Test t = new ReturnCodeTest();
		TestResult tr = t.runTest(msg);
		assertNotNull(tr);
		assertFalse(tr.isPassed());
	}

	public void testSignal() throws IOException {
		String jmf = getTestFileAsString("Signal.jmf");
		InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, true);
		Test t = new ReturnCodeTest();
		TestResult tr = t.runTest(msg);
		assertNotNull(tr);
		assertTrue(tr.isPassed());
	}
}
