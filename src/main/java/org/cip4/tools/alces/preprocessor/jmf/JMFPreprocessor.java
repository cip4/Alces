/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.io.StringReader;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.util.JDFDate;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.preprocessor.PreprocessorContext;
import org.cip4.tools.alces.preprocessor.PreprocessorException;
import org.cip4.tools.alces.util.JDFConstants;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * Preprocesses a JMF message by replacing SenderID, TimeStamp, and IDs of any
 * Query, Command, Signal, Acknowledge, or Response.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @deprecated This Preprocessor has ben split up into separate preprocessors
 *             for the different types of preprocessing.
 */
public class JMFPreprocessor implements Preprocessor {

    public static final String JDF_NS_PREFIX = "jdf";

    public static final String JDF_NS_URI = "http://www.CIP4.org/JDFSchema_1_1";

    public static final String DEFAULT_SENDER_ID = "No sender ID configured";

    private String senderIDValue = DEFAULT_SENDER_ID;

    private static Log log = LogFactory.getLog(JMFPreprocessor.class);

    private XPath senderID = null;

    private XPath timeStamp = null;

    private XPath queryID = null;

    private XPath commandID = null;

    private XPath signalID = null;

    private XPath acknowledgeID = null;

    private XPath responseID = null;

    public JMFPreprocessor() throws PreprocessorException {
        this(DEFAULT_SENDER_ID);
    }

    public JMFPreprocessor(String senderId) throws PreprocessorException {
        senderIDValue = senderId;
        try {
            buildXPaths();
        } catch (JDOMException je) {
            throw new PreprocessorException(
                    "Could not configure XPaths for preprocessing.", je);
        }
    }

    public void setSenderId(String senderId) {
        if (senderId == null) { // XXX Check that value is a valid SenderID
            throw new IllegalArgumentException("SenderID may not be null.");
        }
        senderIDValue = senderId;
    }

    public String getSenderId() {
        return senderIDValue;
    }

    private void buildXPaths() throws JDOMException {
        Namespace jdfNamespace = Namespace.getNamespace(JDF_NS_PREFIX,
                JDF_NS_URI);
        senderID = XPath.newInstance("jdf:JMF/@SenderID");
        senderID.addNamespace(jdfNamespace);
        timeStamp = XPath.newInstance("jdf:JMF/@TimeStamp");
        timeStamp.addNamespace(jdfNamespace);
        queryID = XPath.newInstance("jdf:JMF/jdf:Query/@ID");
        queryID.addNamespace(jdfNamespace);
        commandID = XPath.newInstance("jdf:JMF/jdf:Command/@ID");
        commandID.addNamespace(jdfNamespace);
        signalID = XPath.newInstance("jdf:JMF/jdf:Signal/@ID");
        signalID.addNamespace(jdfNamespace);
        acknowledgeID = XPath.newInstance("jdf:JMF/jdf:Acknowledge/@ID");
        acknowledgeID.addNamespace(jdfNamespace);
        responseID = XPath.newInstance("jdf:JMF/jdf:Response/@ID");
        responseID.addNamespace(jdfNamespace);
    }

    /**
     * Preprocesses a JMF message by replacing SenderID, TimeStamp, and IDs of
     * any Query, Command, Signal, Acknowledge, or Response.
     * 
     * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
     */
    public Message preprocess(final Message message)
            throws PreprocessorException {
        if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
            log
                    .debug("Message not preprocessed because it did not contain JMF. Content-type was: "
                            + message.getContentType());
            return message;
        }
        if (log.isDebugEnabled()) {
            log.debug("Preprocessor input: " + message.toString());
        }
        try {
            // Parse String
            String xml = message.getBody(); // XXX Make sure the
            // body really is XML
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(xml));

            Attribute attr;
            List attrList;
            // JMF/@SenderID
            attr = (Attribute) senderID.selectSingleNode(doc);
            if (attr != null) {
                attr.setValue(senderIDValue);
            }
            // JMF/@TimeStamp
            attr = (Attribute) timeStamp.selectSingleNode(doc);
            if (attr != null) {
                attr.setValue(new JDFDate().getDateTimeISO());
            }
            // JMF/Query/@ID
            IDGenerator20 idgen = IDGenerator20.getInstance();
            attrList = queryID.selectNodes(doc);
            for (Iterator i = attrList.iterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                attr.setValue(idgen.generateUniqueID());
            }
            // JMF/Command/@ID
            attrList = commandID.selectNodes(doc);
            for (Iterator i = attrList.iterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                attr.setValue(idgen.generateUniqueID());
            }
            // JMF/Signal/@ID
            attrList = signalID.selectNodes(doc);
            for (Iterator i = attrList.iterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                attr.setValue(idgen.generateUniqueID());
            }
            // JMF/Acknowledge/@ID
            attrList = acknowledgeID.selectNodes(doc);
            for (Iterator i = attrList.iterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                attr.setValue(idgen.generateUniqueID());
            }
            // JMF/Response/@ID
            attrList = responseID.selectNodes(doc);
            for (Iterator i = attrList.iterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                attr.setValue(idgen.generateUniqueID());
            }
            // Output string
            XMLOutputter outputter = new XMLOutputter(Format.getRawFormat());
            message.setBody(outputter.outputString(doc));
            if (log.isDebugEnabled()) {
                log.debug("Preprocessor output: " + message);
            }
            return message;
        } catch (JDOMParseException jpe) {
            String msg = "The message could not be preprocessed. The message body "
                    + "could not be parsed. Maybe it did not contain XML?";
            throw new PreprocessorException(msg, jpe);
        } catch (Exception e) {
            String msg = "The message could not be preprocessed: " + message;
            log.error(msg);
            throw new PreprocessorException(msg, e);
        }
    }

    /**
     * This is an ID generator for JDF that generates "pure IDs " (se below).
     * Generates a 20 characters long string of hexadecimal values on the form:
     * 
     * <code>ALCESxxxxxxxxxxxxxxxx</code>
     * 
     * The middle 8 characters are the 32 lower bits of
     * System.currentTimeMillis().
     * 
     * The last 8 characters are 4 random bytes generated by
     * java.security.SecureRandom.
     * 
     * Definition of JDF IDs (from "JDF Specification 1.1", section 4.5):
     * 
     * <pre>
     *   
     *    
     *          Pure ID - can contain all characters except the character period &quot;.&quot;
     *          Composite ID - is made up of pure IDs delimited by periods
     *    
     *     Example:
     *          pureID :: = ID-{'.'}
     *          compositeID :: =  pureID['.'pureID]+
     *          ID :: = pureID | compositeID
     *     
     *    
     * </pre>
     * 
     * References: GUIDs without Singletons and Databases
     * http://www.theserverside.com/patterns/thread.jsp?thread_id=4976
     * 
     * Entity Bean Primary Key Generator
     * http://www.theserverside.com/patterns/thread.jsp?thread_id=220
     * 
     * JDF Specification 1.1, section 4.5 http://www.cip4.org
     * 
     * Patterns in Java Volume 1, Grand Singelton, page 127
     * 
     * Patterns in Java Volume 3, Grand Object Identifier, page 137
     */
    private static class IDGenerator20 {

        private SecureRandom mRandomizer = null;

        private static final int TIME_BYTES = 0xFFFFFFFF;

        private static final int RANDOM_BYTES = 4;

        private static final String PREFIX = "ALCES";

        public IDGenerator20() {
            // Initiates random key generator
            mRandomizer = new SecureRandom();
        }

        /**
         * Converts a byte array to hex. Bytes take on values from �128 to 127,
         * inclusive. This byte-to-hex converter first converts to decimal and
         * then to hex.where:
         * 
         * <pre>
         *   
         *    
         *          byte -&gt; dec -&gt; hex
         *             0      0     00
         *           127    127     7F
         *          -128    128     80
         *            -1    255     FF
         *     
         *    
         * </pre>
         * 
         * @param bytes
         *            The array containing the bytes to be converted
         * @param length
         *            The number of bytes in the array to be converted, starting
         *            from index the beginning of the array
         * @return the bytes as a String
         */
        private String toHex(byte[] bytes, int length) {
            int imax;
            if (length < 0 || length > bytes.length) {
                imax = bytes.length;
            } else {
                imax = length;
            }
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < imax; i++) {
                if (bytes[i] < 0) {
                    hex.append(Integer.toHexString(bytes[i] + 256));
                } else if (bytes[i] >= 0 && bytes[i] <= 15) {
                    hex.append("0");
                    hex.append(Integer.toHexString(bytes[i]));
                } else {
                    hex.append(Integer.toHexString(bytes[i]));
                }
            }
            return hex.toString();
        }

        /**
         * Returns a reference to the singelton instance of this class.
         * 
         * @return a reference to the JDFIDGenerator
         */
        public static IDGenerator20 getInstance() {
            return new IDGenerator20();
        }

        /**
         * Generates a unique ID string of hexadecimal values on the form:
         * ALCESxxxxxxxxxxxxxxxx
         * 
         * The middle 8 characters are the 32 lower bits of
         * System.currentTimeMillis().
         * 
         * The last 8 characters are a 4 random bytes generated by
         * java.security.SecureRandom.
         * 
         * @return String
         */
        public String generateUniqueID() {
            StringBuffer uniqueID = new StringBuffer(PREFIX);

            // Is unique to the millisecond
            long timeNow = System.currentTimeMillis();
            int timeLow = (int) timeNow & TIME_BYTES; // Use the lower 16 bits
            uniqueID.append(Integer.toHexString(timeLow));

            // Is unique to the method call for creating the ID
            byte[] bytes = new byte[RANDOM_BYTES];
            mRandomizer.nextBytes(bytes);

            return uniqueID.append(toHex(bytes, RANDOM_BYTES)).toString();
        }
    }

	public Message preprocess(Message message, PreprocessorContext context) throws PreprocessorException {
		return preprocess(message);
	}

}