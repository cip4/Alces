package org.cip4.tools.alces.test.tests;

import org.apache.commons.lang.StringUtils;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.tests.PreviewTest;
import org.cip4.tools.alces.service.testrunner.tests.Test;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;

public class PreviewTestTest extends AlcesTestCase {

    @org.junit.jupiter.api.Test
    public void testRunTest_OnePreview() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceSignal_Preview_first.jmf");
        AbstractJmfMessage message = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assertions.assertTrue(result.getResult() == TestResult.Result.PASSED);
        String log = result.getResultString();
        Assertions.assertEquals(3, StringUtils.countMatches(log, "Ignored Preview"));
        Assertions.assertEquals(1, StringUtils.countMatches(log, "Successfully downloaded Preview"));
    }

    @org.junit.jupiter.api.Test
    public void testRunTest_FourPreviews() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceCMD_Preview_all.jmf");
        AbstractJmfMessage message = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assertions.assertTrue(result.getResult() == TestResult.Result.PASSED);
        String log = result.getResultString();
        Assertions.assertEquals(0, StringUtils.countMatches(log, "Ignored Preview"), log);
        Assertions.assertEquals(4, StringUtils.countMatches(log, "Successfully downloaded Preview"), log);
    }

    @org.junit.jupiter.api.Test
    public void testRunTest_Fail() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceSignal_Preview_all.jmf");
        AbstractJmfMessage message = new IncomingJmfMessage(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assertions.assertFalse(result.getResult() == TestResult.Result.PASSED);
        String log = result.getResultString();
        Assertions.assertEquals(1, StringUtils.countMatches(log, "Could not download"), log);
        Assertions.assertEquals(0, StringUtils.countMatches(log, "Ignored Preview"), log);
        Assertions.assertEquals(3, StringUtils.countMatches(log, "Successfully downloaded Preview"), log);
    }

}
