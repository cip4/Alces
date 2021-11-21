/*
 * Created on Oct 5, 2005
 */
package org.cip4.tools.alces.util;

/**
 * JDF-related constants for file types.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class JDFConstants {

    /**
     * Private constructor so that instances of this class cannot be created.
     */
    private JDFConstants() {  /* */  }
    
    /**
     * Content-type for JDF job ticket
     */
    @Deprecated
    public static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";

    /**
     * Content-type for JMF message
     */
    @Deprecated
    public static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";

    /**
     * Content-type for MIME package
     */
    public static final String MIME_CONTENT_TYPE = "multipart/related";
    
    /**
     * Content-type for JMF MIME package
     */
    @Deprecated
    public static final String JMF_MIME_CONTENT_TYPE = MIME_CONTENT_TYPE;
    
    /**
     * Content-type for JDF MIME package
     */
    @Deprecated
    public static final String JDF_MIME_CONTENT_TYPE = MIME_CONTENT_TYPE;
    
    
    
    /**
     * Content-type for XML document
     */
    public static final String XML_CONTENT_TYPE = "text/xml";

    /**
     * File extension for JDF job ticket
     */
    public static final String JDF_EXTENSION = ".jdf";

    /**
     * File extension for JMF message
     */
    public static final String JMF_EXTENSION = ".jmf";

    /**
     * File extension for JDF MIME package
     */
    public static final String JDF_MIME_EXTENSION = ".mjd";

    /**
     * File extension for JMF MIME package
     */
    public static final String JMF_MIME_EXTENSION = ".mjm";
    
    /**
     * File extension for XML document
     */
    public static final String XML_EXTENSION = ".xml";
    
}
