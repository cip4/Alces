package org.cip4.tools.alces.message;

import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;

public class AbstractMessageTest extends AlcesTestCase {

    @Test
    public void testGetBodyAsJMF() throws IOException {
        JDFJMF jmf = getTestFileAsJMF("QueryKnownDevices.jmf");
        AbstractJmfMessage m = new DummyMessage(JDFConstants.JMF_CONTENT_TYPE, null, jmf.toXML(), true);
        Assertions.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(jmf.toXML(), m.getBody());
        Assertions.assertEquals(jmf.toXML(), JmfUtil.getBodyAsJMF(m).toXML());
        Assertions.assertNull(JmfUtil.getBodyAsJMF(m));
    }

    @Test
    public void testGetBodyAsJMF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        AbstractJmfMessage m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(0));
        Assertions.assertEquals(jdf.getJMFRoot().toXML(), JmfUtil.getBodyAsJMF(m).toXML());
    }

    @Test
    public void testGetBodyAsJDF() throws IOException {
        JDFNode jdf = getTestFileAsJDF("Elk_Approval.jdf");
        AbstractJmfMessage m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, jdf.toXML(), true);
        Assertions.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(jdf.toXML(), m.getBody());
        Assertions.assertNull(JmfUtil.getBodyAsJMF(m));
    }

    @Test
    public void testGetBodyAsJDF_Mime() throws IOException, MessagingException {
        final String mimeFile = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        String mime = getTestFileAsString(mimeFile);
        AbstractJmfMessage m = new DummyMessage(JDFConstants.MIME_CONTENT_TYPE, null, mime, true);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        Assertions.assertEquals(mime, m.getBody());
        // Extract JDF from MIME
        Multipart multipart = MimeUtil.getMultiPart(getTestFileAsStream(mimeFile));
        JDFDoc jdf = MimeUtil.getJDFDoc(multipart.getBodyPart(1));
    }

    @Test
    public void testGetContentType() {
        AbstractJmfMessage m = new DummyMessage(JDFConstants.JDF_CONTENT_TYPE, null, null, true);
        Assertions.assertEquals(JDFConstants.JDF_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.MIME_CONTENT_TYPE);
        Assertions.assertEquals(JDFConstants.MIME_CONTENT_TYPE, m.getContentType());
        m.setContentType(JDFConstants.JMF_CONTENT_TYPE);
        Assertions.assertEquals(JDFConstants.JMF_CONTENT_TYPE, m.getContentType());
    }


    class DummyMessage extends AbstractJmfMessage {
        public DummyMessage(String contentType, String header, String body,
                            boolean isSessionInitiator) {
            super(contentType, header, body, isSessionInitiator);
        }

    }
}
