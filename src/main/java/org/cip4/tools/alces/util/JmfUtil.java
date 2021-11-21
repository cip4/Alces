package org.cip4.tools.alces.util;

import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Multipart;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

public class JmfUtil {

    private static final Logger log = LoggerFactory.getLogger(JmfUtil.class);

    /**
     * Returns the message's body as a JDOM Document
     *
     * @return a JDOM tree; <code>null</code> if the message does not contain XML
     */
    public static Document getBodyAsJDOM(AbstractJmfMessage jmfMessage) {

        org.jdom.Document doc = null;
        try {
            // Parse String
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(new StringReader(jmfMessage.getBody()));
        } catch (Exception e) {
            log.error("Could not build JDOM from message body.", e);
        }
        return doc;
    }

    /**
     * Returns the message body as JMF.
     *
     * @return the JMF message; <code>null</code> if the message does not contain JMF
     */
    public static JDFJMF getBodyAsJMF(AbstractJmfMessage jmfMessage) {
        JDFJMF jmf = null;

        try {
            if (jmfMessage.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
                jmf = new JDFParser().parseString(jmfMessage.getBody()).getJMFRoot();

            } else if (jmfMessage.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
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
