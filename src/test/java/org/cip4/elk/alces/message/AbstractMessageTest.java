package org.cip4.elk.alces.message;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.cip4.elk.alces.junit.AlcesTestCase;
import org.cip4.elk.alces.util.JDFConstants;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;

public class AbstractMessageTest extends AlcesTestCase {

	public void testGetBodyAsJMF() throws IOException {
		JDFJMF jmf = getTestFileAsJMF("QueryKnownDevices.jmf");
		Message m = new DummyMessage(JDFConstants.JMF_CONTENT_TYPE, null, jmf.toXML(), true);
		assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
		assertEquals(jmf.toXML(), m.getBody());
		assertEquals(jmf.toXML(), m.getBodyAsJMF().toXML());
		assertNull(m.getBodyAsJDF());
	}

	public void testGetBodyAsJMF_Mime() throws IOException, MessagingException {
		final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
		String mime = getTestFileAsString(mimeFile);		
		Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
		assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
		assertEquals(mime, m.getBody());		
		// Extract JDF from MIME
		Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
		JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(0));
		assertEquals(jdf.getJMFRoot().toXML(), m.getBodyAsJMF().toXML());
	}
	
	public void testGetBodyAsJDF() throws IOException {
		JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
		Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, jdf.toXML(), true);
		assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
		assertEquals(jdf.toXML(), m.getBody());
		assertEquals(jdf.toXML(), m.getBodyAsJDF().toXML());
		assertNull(m.getBodyAsJMF());
	}
	
	public void testGetBodyAsJDF_Mime() throws IOException, MessagingException {
		final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
		String mime = getTestFileAsString(mimeFile);		
		Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
		assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
		assertEquals(mime, m.getBody());		
		// Extract JDF from MIME
		Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
		JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(1));
		assertEquals(jdf.getJDFRoot().toXML(), m.getBodyAsJDF().toXML());
	}

	public void testGetContentType() {
		Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, null, true);
		assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
		m.setContentType(JDFConstants.MIME_CONTENT_TYPE);
		assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
		m.setContentType(JDFConstants.JMF_CONTENT_TYPE);
		assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());	
	}

	
	
	
	class DummyMessage extends AbstractMessage {
		public DummyMessage(String contentType, String header, String body,
				boolean isSessionInitiator) {
			super(contentType, header, body, isSessionInitiator);
		}

	}
}
