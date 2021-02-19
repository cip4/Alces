package org.cip4.tools.alces.test.tests;

import org.apache.commons.lang.StringUtils;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

public class PreviewTestTest extends AlcesTestCase {

    @org.junit.Test
    public void testRunTest_OnePreview() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceSignal_Preview_first.jmf");
        Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assert.assertTrue(result.isPassed());
        String log = result.getResultString();
        Assert.assertEquals(3, StringUtils.countMatches(log, "Ignored Preview"));
        Assert.assertEquals(1, StringUtils.countMatches(log, "Successfully downloaded Preview"));
    }

    @org.junit.Test
    public void testRunTest_FourPreviews() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceCMD_Preview_all.jmf");
        Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assert.assertTrue(result.isPassed());
        String log = result.getResultString();
        Assert.assertEquals(log, 0, StringUtils.countMatches(log, "Ignored Preview"));
        Assert.assertEquals(log, 4, StringUtils.countMatches(log, "Successfully downloaded Preview"));
    }

    @org.junit.Test
    public void testRunTest_Fail() throws IOException {
        // Load JMF
        String jmf = getTestFileAsString("ResourceSignal_Preview_all.jmf");
        Message message = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf, false);
        // Configure test
        File previewDir = new File(System.getProperty("java.io.tmpdir"), "PreviewTestTest");
        Test test = new PreviewTest(previewDir);
        // Run test
        TestResult result = test.runTest(message);
        Assert.assertFalse(result.isPassed());
        String log = result.getResultString();
        Assert.assertEquals(log, 1, StringUtils.countMatches(log, "Could not download"));
        Assert.assertEquals(log, 0, StringUtils.countMatches(log, "Ignored Preview"));
        Assert.assertEquals(log, 3, StringUtils.countMatches(log, "Successfully downloaded Preview"));
    }

}
