package org.cip4.elk.alces.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.message.OutMessage;
import org.cip4.elk.alces.message.OutMessageImpl;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.jdflib.util.UrlUtil;

public class TestRunnerTest extends AlcesTestCase {

	@SuppressWarnings("unchecked")
	public void testPackageAsMime() throws IOException, MessagingException {
		final String jmf = getTestFileAsString("CommandSubmitQueueEntry-Approval.jmf");
		final File jdfFile = new File(
				"src/test/data/TestRunnerTest/Elk_Approval.jdf");
		assertTrue(jdfFile.exists());
		TestRunner testRunner = new TestRunner();
		OutMessage outMessage = new OutMessageImpl(null, jmf, true);
		OutMessage mimeOutMessage = testRunner.packageAsMime(outMessage,
				jdfFile);
		JDFNode jdf = mimeOutMessage.getBodyAsJDF();
		assertNotNull(jdf);
		List<JDFFileSpec> fileSpecs = (Vector) jdf.getChildrenByTagName(
				ElementName.FILESPEC, null, null, false, false, 0);
		assertEquals(3, fileSpecs.size());
		for (JDFFileSpec fileSpec : fileSpecs) {
			assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL()
					.startsWith("cid:"));
			assertTrue("Incorrect URL: " + fileSpec.getURL(), fileSpec.getURL()
					.endsWith(".pdf"));
		}
		BodyPart[] parts = MimeUtil
				.extractMultipartMime(new ByteArrayInputStream(mimeOutMessage
						.getBody().getBytes()));
		assertEquals(5, parts.length);
		assertEquals("message.jmf", parts[0].getFileName());
		assertEquals("Elk_Approval.jdf", parts[1].getFileName());
		assertEquals("file1.pdf", parts[2].getFileName());
		assertEquals("file2.pdf", parts[3].getFileName());
		assertEquals("file3.pdf", parts[4].getFileName());
	}

	public void testUrlUtil() {
		String filePath = "src" + File.separator + "test" + File.separator
				+ "data" + File.separator + "TestRunnerTest" + File.separator
				+ "content" + File.separator + "file1.pdf";
		File file1 = UrlUtil.urlToFile(filePath);
		assertNotNull(file1);
		assertEquals(filePath, file1.getPath());
		assertTrue(file1.exists());
	}
}
