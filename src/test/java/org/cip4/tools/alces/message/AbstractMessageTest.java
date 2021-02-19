package org.cip4.tools.alces.message;

import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;

public class AbstractMessageTest extends AlcesTestCase {

    @Test
    public void testGetBodyAsJMF() throws IOException {
        JDFJMF jmf = getTestFileAsJMF("QueryKnownDevices.jmf");
        Message m = new DummyMessage(JDFConstants.JMF_CONTENT_TYPE, null, jmf.toXML(), true);
        Assert.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
        Assert.assertEquals(jmf.toXML(), m.getBody());
        Assert.assertEquals(jmf.toXML(), m.getBodyAsJMF().toXML());
        Assert.assertNull(m.getBodyAsJDF());
    }

    @Test
    public void testGetBodyAsJMF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assert.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assert.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(0));
        Assert.assertEquals(jdf.getJMFRoot().toXML(), m.getBodyAsJMF().toXML());
    }

    @Test
    public void testGetBodyAsJDF() throws IOException {
        JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
        Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, jdf.toXML(), true);
        Assert.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        Assert.assertEquals(jdf.toXML(), m.getBody());
        Assert.assertEquals(jdf.toXML(), m.getBodyAsJDF().toXML());
        Assert.assertNull(m.getBodyAsJMF());
    }

    @Test
    public void testGetBodyAsJDF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        Message m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assert.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assert.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(1));
        Assert.assertEquals(jdf.getJDFRoot().toXML(), m.getBodyAsJDF().toXML());
    }

    @Test
    public void testGetContentType() {
        Message m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, null, true);
        Assert.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.MIME_CONTENT_TYPE);
        Assert.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.JMF_CONTENT_TYPE);
        Assert.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
    }


    class DummyMessage extends AbstractMessage {
        public DummyMessage(String contentType, String header, String body,
                            boolean isSessionInitiator) {
            super(contentType, header, body, isSessionInitiator);
        }

    }
}
