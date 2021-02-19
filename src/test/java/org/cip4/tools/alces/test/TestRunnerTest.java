package org.cip4.tools.alces.test;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class TestRunnerTest extends AlcesTestCase {

    @Test
    @SuppressWarnings("unchecked")
    public void testPackageAsMime() throws IOException, MessagingException {
        final String jmf = getTestFileAsString("CommandSubmitQueueEntry-Approval.jmf");
        final File jdfFile = getTestFileAsFile("Elk_Approval.jdf");

        Assert.assertTrue(jdfFile.exists());
        TestRunner testRunner = new TestRunner();
        OutMessage outMessage = new OutMessageImpl(null, jmf, true);
        OutMessage mimeOutMessage = testRunner.packageAsMime(outMessage, jdfFile);
        JDFNode jdf = mimeOutMessage.getBodyAsJDF();
        Assert.assertNotNull(jdf);
        List<JDFFileSpec> fileSpecs = (Vector) jdf.getChildrenByTagName(ElementName.FILESPEC, null, null, false, false, 0);
        Assert.assertEquals(3, fileSpecs.size());
        for (JDFFileSpec fileSpec : fileSpecs) {
            Assert.assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL().startsWith("cid:"));
            Assert.assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL().endsWith(".pdf"));
        }
        BodyPart[] parts = MimeUtil.extractMultipartMime(new ByteArrayInputStream(mimeOutMessage.getBody().getBytes()));
        Assert.assertEquals(5, parts.length);
        Assert.assertEquals("message.jmf", parts[0].getFileName());
        Assert.assertEquals("Elk_Approval.jdf", parts[1].getFileName());
        Assert.assertEquals("file1.pdf", parts[2].getFileName());
        Assert.assertEquals("file2.pdf", parts[3].getFileName());
        Assert.assertEquals("file3.pdf", parts[4].getFileName());
    }

    @Test
    public void testUrlUtil() {
        final File file1 = getTestFileAsFile("content/file1.pdf");

        Assert.assertNotNull(file1);
        Assert.assertTrue(file1.exists());
    }
}
