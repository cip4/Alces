package org.cip4.elk.alces.test.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.InMessageImpl;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.test.TestResult;
import org.cip4.elk.alces.util.JDFConstants;

public class PreviewTestTest extends AlcesTestCase {

	public void testRunTest_OnePreview() throws IOException {
		// Load JMF
		String jmf = getTestFileAsString("ResourceSignal_Preview_first.jmf");		
		Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
		// Configure test
		File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest"); 		
		Test test = new PreviewTest(previewDir);
		// Run test
		TestResult result = test.runTest(message);
		assertTrue(result.isPassed());
		String log = result.getResultString();
		assertEquals(3, StringUtils.countMatches(log, "Ignored Preview"));
		assertEquals(1, StringUtils.countMatches(log, "Successfully downloaded Preview"));
	}
	
	public void testRunTest_FourPreviews() throws IOException {
		// Load JMF
		String jmf = getTestFileAsString("ResourceCMD_Preview_all.jmf");		
		Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
		// Configure test
		File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest"); 		
		Test test = new PreviewTest(previewDir);
		// Run test
		TestResult result = test.runTest(message);
		assertTrue(result.isPassed());
		String log = result.getResultString();
		assertEquals(log, 0, StringUtils.countMatches(log, "Ignored Preview"));
		assertEquals(log, 4, StringUtils.countMatches(log, "Successfully downloaded Preview"));
	}

	public void testRunTest_Fail() throws IOException {
		// Load JMF
		String jmf = getTestFileAsString("ResourceSignal_Preview_all.jmf");		
		Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
		// Configure test
		File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest"); 		
		Test test = new PreviewTest(previewDir);
		// Run test
		TestResult result = test.runTest(message);
		assertFalse(result.isPassed());
		String log = result.getResultString();
		assertEquals(log, 1, StringUtils.countMatches(log, "Could not download"));
		assertEquals(log, 0, StringUtils.countMatches(log, "Ignored Preview"));
		assertEquals(log, 3, StringUtils.countMatches(log, "Successfully downloaded Preview"));
	}
	
}
