package org.cip4.elk.alces.message.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Formatter for the XML-Output of incoming and outgoing messages
 * 
 * @author Marco Kornrumpf (Marco.Kornrumpf@bertelsmann.de)
 */
@Deprecated
public class FormatOutput {

    private static Log log = LogFactory.getLog(FormatOutput.class);
    private static StringWriter sw = null;
    private static StringReader sr = null;
    private static Document doc = null;
    private static XMLOutputter xmlOut = null;

    /**
     * Formats a message using JDOM XMLOutputter. If the message could not be
     * formatted the original message is returned.
     * 
     * @param xml the XML string to format
     * @return the formatted XML string
     */
    public static String formatMessage(String xml) {
        try {
            sw = new StringWriter();
            sr = new StringReader(xml);

            doc = new SAXBuilder().build(sr);
            xmlOut = new XMLOutputter();

            xmlOut.setFormat(Format.getPrettyFormat());

            xmlOut.output(doc, sw);

            return (sw.getBuffer().toString());
        } catch (JDOMException je) {
            log.debug("Could not format message because it did not contint XML.", je);
        } catch (IOException ioe) {
            log.debug("Could not format message.", ioe);
        }
        return xml;
    }

}
