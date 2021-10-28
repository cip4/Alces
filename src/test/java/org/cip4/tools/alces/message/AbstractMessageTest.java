package org.cip4.tools.alces.message;

import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;

public class AbstractMessageTest extends AlcesTestCase {

    @Test
    public void testGetBodyAsJMF() throws IOException {
        JDFJMF jmf = getTestFileAsJMF("QueryKnownDevices.jmf");
        Message m = new DummyMessage(JDFConstants.JMF_CONTENT_TYPE, null, jmf.toXML(), true);
        Assertions.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(jmf.toXML(), m.getBody());
        Assertions.assertEquals(jmf.toXML(), m.getBodyAsJMF().toXML());
        Assertions.assertNull(m.getBodyAsJDF());
    }

    @Test
    public void testGetBodyAsJMF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(0));
        Assertions.assertEquals(jdf.getJMFRoot().toXML(), m.getBodyAsJMF().toXML());
    }

    @Test
    public void testGetBodyAsJDF() throws IOException {
        JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
        Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, jdf.toXML(), true);
        Assertions.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(jdf.toXML(), m.getBody());
        Assertions.assertEquals(jdf.toXML(), m.getBodyAsJDF().toXML());
        Assertions.assertNull(m.getBodyAsJMF());
    }

    @Test
    public void testGetBodyAsJDF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(1));
        Assertions.assertEquals(jdf.getJDFRoot().toXML(), m.getBodyAsJDF().toXML());
    }

    @Test
    public void testGetContentType() {
        Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, null, true);
        Assertions.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.MIME_CONTENT_TYPE);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.JMF_CONTENT_TYPE);
        Assertions.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
    }


    class DummyMessage extends AbstractMessage {
        public DummyMessage(String contentType, String header, String body,
                            boolean isSessionInitiator) {
            super(contentType, header, body, isSessionInitiator);
        }

    }
}
