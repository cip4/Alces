/*
 * Created on Sep 2, 2004
 */
package org.cip4.elk.alces.jmf;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumDeviceDetails;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.jmf.JDFJMF;

/**
 * A default implementation of <code>JMFMessageFactory</code>. This
 * implementation creates JDF elements using the constructor of
 * <code>com.heidelberg.JDFLib.core.JDFDoc</code>:
 * 
 * <pre>
 * new JDFDoc(elementName).getRoot()
 * </pre>
 * 
 * @see com.heidelberg.JDFLib.core.JDFDoc
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class DefaultJMFMessageFactory extends JMFMessageFactory {

    protected final static String JDF_NAMESPACE_PREFIX = "";

    protected final static String JDF_NAMESPACE_URI = "http://www.CIP4.org/JDFSchema_1_1";

    protected final static String XSI_NAMESPACE_PREFIX = "xsi";

    protected final static String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    protected DefaultJMFMessageFactory() {
        super();
    }

    /**
     * Creates a JDF element of the specified type.
     * 
     * @see org.cip4.elk.JMFMessageFactory#createJDFElement(java.lang.String)
     */
    public JDFElement createJDFElement(String elementName) {
        return (JDFElement) new JDFDoc(elementName).getRoot();
    }

    /**
     * Convenience method for creating JMF nodes. This method sets the
     * attributes:
     * <ul>
     * <li>JMF/@xmlns="http://www.CIP4.org/JDFSchema_1_1"</li>
     * <li>JMF/@xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"</li>
     * <li>JMF/@Version="1.2"</li>
     * <li>JMF/@TimeStamp</li>
     * <li>JMF/@SenderID</li>
     * </ul>
     * 
     * @return a JMF node
     */
    public JDFJMF createJMF() {
        JDFJMF jmf = (JDFJMF) createJDFElement(ElementName.JMF);
        jmf.addNameSpace(XSI_NAMESPACE_PREFIX, XSI_NAMESPACE_URI);
        jmf.setSenderID("Not configured");
        return jmf;
    }

    public JDFJMF createJMF(String xsiType) {
        // Creates JMF
        JDFJMF jmf = createJMF();
        // Appends message to JMF
        if (xsiType.startsWith("Query")) {
            jmf.appendQuery().setType(xsiType.substring("Query".length()));
            // QueryStatus with StatusQuParams
            if (xsiType.equals("QueryStatus")) {
                jmf.getQuery(0).appendStatusQuParams().setDeviceDetails(
                        EnumDeviceDetails.Details);
            }
            if (xsiType.equals("QueryKnownDevices")) {
                jmf
                        .getQuery(0)
                        .appendDeviceFilter()
                        .setDeviceDetails(
                                org.cip4.jdflib.auto.JDFAutoDeviceFilter.EnumDeviceDetails.Brief);
            }
        } else if (xsiType.startsWith("Command")) {
            jmf.appendCommand().setType(xsiType.substring("Command".length()));            
            // Builds the StopPersChParams and add the URL-Attribute
            if (xsiType.equals("CommandStopPersistentChannel")) {
                try {
                    jmf.getCommand(0).appendStopPersChParams().setURL(
                            "http://"
                                    + InetAddress.getLocalHost()
                                            .getHostAddress());
                } catch (UnknownHostException e) {
                    System.err.println(e);
                }

            }
            if (xsiType.equals("CloseQueue")) {
                try {
                    jmf.getElement("Command").appendElement("StopPersChParams")
                            .appendAttribute(
                                    "URL",
                                    "http://"
                                            + InetAddress.getLocalHost()
                                                    .getHostAddress() + "", "",
                                    JDFConstants.EMPTYSTRING, true);

                } catch (UnknownHostException e) {
                    System.err.println(e);

                }

            }

        } else if (xsiType.startsWith("Acknowledge")) {
            jmf.appendAcknowledge().setType(xsiType.substring("Acknowledge".length()));            
        } else if (xsiType.startsWith("Response")) {
            jmf.appendResponse().setType(xsiType.substring("Response".length()));
        } else if (xsiType.startsWith("Signal")) {
            jmf.appendSignal().setType(xsiType.substring("Signal".length()));
        } else {
            throw new IllegalArgumentException("Could not create message"
                    + " with xsi:type='" + xsiType + "'.");
        }
        return jmf;
    }
}