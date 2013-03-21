/*
 * Created on Sep 13, 2004
 */
package org.cip4.elk.alces.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.elk.alces.message.InMessage;
import org.cip4.elk.alces.message.InMessageImpl;
import org.cip4.elk.alces.message.Message;
import org.cip4.elk.alces.transport.util.HttpHeaderUtils;

/**
 * A service for sending messages over HTTP.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class HttpDispatcher {

	private static Log log = LogFactory.getLog(HttpDispatcher.class);

	final private HttpClient _httpClient;

	/**
	 * Creates a new single-threaded dispatcher for sending HTTP requests.
	 */
	public HttpDispatcher() {
		this(null, -1, null, null);
	}

	/**
	 * Creates a new multi-threaded dispatcher for sending HTTP requests.
	 * 
	 * @param maxConnections
     *            the maximum number of concurrent connections; if <code>-1</code> then the dispatcher will not be multi-threaded
     * @param maxConnectionsPerHost
     *            the maximum number of concurrent connections per host; ignored if connectionPoolSize is <code>-1</code>
     */
	public HttpDispatcher(int maxConnections, int maxConnectionsPerHost) {
		this(null, -1, null, null, maxConnections, maxConnectionsPerHost);
	}
	
    /**
     * Creates a new single-threaded dispatcher for sending HTTP requests.
     * 
     * @param proxyHost
     *            the proxy's address; <code>null</code> if no proxy is used
     * @param proxyPort
     *            the proxy's port; <code>-1</code> if no proxy is used
     * @param proxyUser
     *            username for proxy authentication; <code>null</code> if authentication is
     *            not used
     * @param proxyPassword
     *            password for proxy authentication; <code>null</code> if authentication is
     *            not used
     */
    public HttpDispatcher(String proxyHost, int proxyPort, String proxyUser,
            String proxyPassword) {
        this(proxyHost, proxyPort, proxyUser, proxyPassword, -1, -1);
    }
    
	/**
     * Creates a new multi-threaded dispatcher for sending HTTP requests.
     * 
     * @param proxyHost
     *            the proxy's address; <code>null</code> if no proxy is used
     * @param proxyPort
     *            the proxy's port; <code>-1</code> if no proxy is used
     * @param proxyUser
     *            Username for proxy authentication; <code>null</code> if authentication is
     *            not used. If NTLM authentication should be used, <code>proxyUser</code> should be 
     *            on the format <code>domain\\username</code>. 
     * @param proxyPassword
     *            password for proxy authentication; <code>null</code> if authentication is
     *            not used
     * @param maxConnections
     *            the maximum number of concurrent connections; if <code>-1</code> then the dispatcher will not be multi-threaded
     * @param maxConnectionsPerHost
     *            the maximum number of concurrent connections per host; ignored if connectionPoolSize is <code>-1</code>
     */
    public HttpDispatcher(String proxyHost, int proxyPort, String proxyUser,
            String proxyPassword, int maxConnections, int maxConnectionsPerHost) {        
		if (maxConnections != -1) {
            final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
            final HttpConnectionManagerParams managerParams = new HttpConnectionManagerParams();
            managerParams.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
            managerParams.setMaxTotalConnections(maxConnections);
            connectionManager.setParams(managerParams);
            _httpClient = new HttpClient(connectionManager);
        } else {
            _httpClient = new HttpClient();
            
        }		
		HttpClientParams clientParams = new HttpClientParams();
        clientParams.setSoTimeout(0); // infinite timeout        
		_httpClient.setParams(clientParams);
		
        try {
			configureProxy(proxyHost, proxyPort, proxyUser, proxyPassword);
		} catch (UnknownHostException e) {
			log.error("Could not configure proxy: " + e.toString(), e);
		}        
    }

	private void configureProxy(String proxyHost, int proxyPort, String proxyUser, String proxyPassword) throws UnknownHostException {

        final HostConfiguration hostConfig = _httpClient.getHostConfiguration();
        if (StringUtils.isNotEmpty(proxyHost) && proxyPort != -1) {        	
            hostConfig.setProxy(proxyHost, proxyPort);
            log.debug("Using proxy configuration: " + proxyHost + ":"
                    + proxyPort);
            if (StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPassword)) {
            	final Credentials credentials;
            	if (proxyUser.indexOf("\\") != -1) {
            		// Extract domain from username
            		String[] domainAndUser = proxyUser.split("\\\\");
            		credentials = new NTCredentials(domainAndUser[1], proxyPassword, InetAddress.getLocalHost().getHostName(), domainAndUser[0]);
            	} else {
            		credentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            	}
            	_httpClient.getState().setProxyCredentials(
                		new AuthScope(proxyHost, proxyPort, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME), 
                		credentials);
                log.debug("Using proxy authentication: " + proxyUser + "/"
                        + proxyPassword);                
            }
        }
	}

	/**
	 * Posts a <code>Message</code> to a URL.
	 * 
	 * @param message
	 *            the message to post
	 * @param url
	 *            the URL to post to
	 * @return the response to the posted message
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public InMessage dispatch(final Message message, final String url) throws IOException {
		InMessage response = null;
        HttpMethod postMethod = null;
		try {
			postMethod = postData(message, url);
			// Update the outgoing message with the final header used

			final String outHeader = HttpHeaderUtils.convertHttpHeadersToString(postMethod
					.getRequestHeaders());
			message.setHeader(outHeader);
			// Set header for incoming message
			final String inHeader = HttpHeaderUtils.convertHttpHeadersToString(postMethod
					.getResponseHeaders());
			// Determines if a connection was established or not
			if (postMethod.getResponseHeaders().length != 0) {
				final Header inContentTypeHeader = postMethod.getResponseHeader(
						HttpHeaderUtils.CONTENT_TYPE_HEADER);
                String inContentType = null;
				if (inContentTypeHeader != null) {
				    inContentType = inContentTypeHeader.getValue();
				}
				final String body = postMethod.getResponseBodyAsString();
				response = new InMessageImpl(inContentType, inHeader, body, false);
			}
//		} catch (Exception e) {
//			log.error("An error occurred while posting message to URL:" + url
//					+ " ", e);
		} finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
		return response;
	}

	private PostMethod postData(Message message, String url)
			throws HttpException, IOException {
        PostMethod post = null;
	    try {
	        post = new PostMethod(url);
	        post.setRequestHeader(HttpHeaderUtils.CONTENT_TYPE_HEADER, message
	                .getContentType()); // ; charset=UTF-8");
	        post.setRequestEntity(new StringRequestEntity(message.getBody()));
	        if (log.isDebugEnabled()) {
	            log.debug("Posting message to URL '" + url + "':\n" + "Headers:\n"
	                    + Arrays.asList(post.getRequestHeaders()) + "\nBody:\n"
	                    + post.getRequestEntity());
	        }
	        int result = _httpClient.executeMethod(post);
	        // Debug logging
	        if (log.isDebugEnabled()) {
	            log.debug("Response from posting message to URL '" + url
	                    + "':\n" + "Headers:\n"
	                    + Arrays.asList(post.getResponseHeaders())
	                    + "\nBody:\n" + post.getResponseBodyAsString() + "\n"
	                    + "Status code: " + result);
	        }
	        if (result == HttpStatus.SC_NOT_FOUND) {
	            throw new IOException("HTTP ERROR 404");
	        }
	    } catch (IllegalArgumentException e) {
	        log.error("Can not connect to URL:" + url + " " + e);
	    }
	    return post;
	}

}
