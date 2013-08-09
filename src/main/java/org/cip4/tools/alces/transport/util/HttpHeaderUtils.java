/*
 * Created on Jun 9, 2005
 */
package org.cip4.tools.alces.transport.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class HttpHeaderUtils {

    public static String CONTENT_TYPE_HEADER = "Content-type";
    
    /**
     * Converts the headers in a <code>HttpServletRequest</code> to a
     * <code>String</code>.
     * 
     * @param request
     * @return
     */
    public static String convertHttpHeadersToString(HttpServletRequest request) {
        StringBuffer header = new StringBuffer();
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
            String headerName = (String) e.nextElement();
            header.append(headerName);
            header.append(": ");
            header.append(request.getHeader(headerName));
            header.append("\n");
        }
        return header.toString();
    }

    /**
     * Converts an array of <code>HttpClient</code> <code>Header</code> s to
     * a <code>String</code>.
     * 
     * @param headers
     * @return
     */
    public static String convertHttpHeadersToString(Header[] headers) {
        StringBuffer header = new StringBuffer();
        for (int i = 0; i < headers.length; i++) {
            header.append(headers[i].getName());
            header.append(": ");
            header.append(headers[i].getValue());
            header.append("\n");            
        }
        return header.toString();
    }
}
