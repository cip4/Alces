package org.cip4.tools.alces.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;

public class TestRunnerTest extends AlcesTestCase {

	@SuppressWarnings("unchecked")
	public void testPackageAsMime() throws IOException, MessagingException {
		final String jmf = getTestFileAsString("CommandSubmitQueueEntry-Approval.jmf");
		final File jdfFile = getTestFileAsFile("Elk_Approval.jdf");

		assertTrue(jdfFile.exists());
		TestRunner testRunner = new TestRunner();
		OutMessage outMessage = new OutMessageImpl(null, jmf, true);
		OutMessage mimeOutMessage = testRunner.packageAsMime(outMessage, jdfFile);
		JDFNode jdf = mimeOutMessage.getBodyAsJDF();
		assertNotNull(jdf);
		List<JDFFileSpec> fileSpecs = (Vector) jdf.getChildrenByTagName(ElementName.FILESPEC, null, null, false, false, 0);
		assertEquals(3, fileSpecs.size());
		for (JDFFileSpec fileSpec : fileSpecs) {
			assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL().startsWith("cid:"));
			assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL().endsWith(".pdf"));
		}
		BodyPart[] parts = MimeUtil.extractMultipartMime(new ByteArrayInputStream(mimeOutMessage.getBody().getBytes()));
		assertEquals(5, parts.length);
		assertEquals("message.jmf", parts[0].getFileName());
		assertEquals("Elk_Approval.jdf", parts[1].getFileName());
		assertEquals("file1.pdf", parts[2].getFileName());
		assertEquals("file2.pdf", parts[3].getFileName());
		assertEquals("file3.pdf", parts[4].getFileName());
	}

	public void testUrlUtil() {
		final File file1 = getTestFileAsFile("content/file1.pdf");

		assertNotNull(file1);
		assertTrue(file1.exists());
	}
}
