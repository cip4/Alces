package org.cip4.tools.alces.util;

import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.mail.Multipart;
import java.io.ByteArrayInputStream;

public class JmfUtil {

    private static final Logger log = LoggerFactory.getLogger(JmfUtil.class);

    /**
     * Returns the message body as JMF.
     *
     * @return the JMF message; <code>null</code> if the message does not contain JMF
     */
    public static JDFJMF getBodyAsJMF(AbstractJmfMessage jmfMessage) {
        JDFJMF jmf = null;

        try {
            if (jmfMessage.getContentType().startsWith(JDFConstants.MIME_JMF)) {
                jmf = new JDFParser().parseString(jmfMessage.getBody()).getJMFRoot();

            } else if (jmfMessage.getContentType().startsWith(MediaType.MULTIPART_RELATED_VALUE)) {
                Multipart multipart = MimeUtil.getMultiPart(new ByteArrayInputStream(jmfMessage.getBody().getBytes()));
                JDFDoc jdfDoc = MimeUtil.getJDFDoc(multipart.getBodyPart(0));

                if (jdfDoc != null) {
                    jmf = jdfDoc.getJMFRoot();
                }
            } else {
                // Try parsing anyway
                jmf = new JDFParser().parseString(jmfMessage.getBody()).getJMFRoot();
            }
        } catch (Exception e) {
            log.error("Could not build JMF from message body.", e);
        }

        return jmf;
    }
}
