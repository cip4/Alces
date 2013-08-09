/*
 * Created on Apr 23, 2005
 */
package org.cip4.tools.alces.message;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.mail.Multipart;

import org.apache.log4j.Logger;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.jdom.input.SAXBuilder;

/**
 * A base class for JMF messages sent and received by Alces.
 * 
 * @author Claes Buckwalter
 * @version $Id$
 */
public abstract class AbstractMessage implements Message {

	protected static Logger LOGGER = Logger.getLogger(AbstractMessage.class);

	protected String _header;

	protected String _body;

	protected String _contentType;

	// XXX private TestSession _testSession;
	protected List<TestResult> _testResults;

	protected boolean _isSessionInitiator = false;

	/**
	 * Creates a new Message.
	 * 
	 * @param contentType
	 * @param header The <code>Message</code>'s header. This usually corresponds to the HTTP header fields.
	 * @param body the content of the <code>Message</code>'s body
	 * @param isSessionInitiator true if this <code>Message</code> initiated a <code>TestSession</code>
	 */
	public AbstractMessage(String contentType, String header, String body, boolean isSessionInitiator) {
		_contentType = contentType;
		_header = header;
		_body = body;
		_testResults = new Vector<TestResult>();
		_isSessionInitiator = isSessionInitiator;
	}

	/**
	 * Creates a new Message. The content-type defaults to the JMF content-type (application/vnd.cip4-jmf+xml).
	 * 
	 * @param header the <code>Message</code>'s header. This usually corresponds to the HTTP header fields.
	 * @param body the content of the <code>Message</code>'s body
	 * @param isSessionInitiator true if this <code>Message</code> initiated a <code>TestSession</code>
	 */
	public AbstractMessage(String header, String body, boolean isSessionInitiator) {
		this(JDFConstants.JMF_CONTENT_TYPE, header, body, isSessionInitiator);
	}

	@Override
	public String toString() {
		return "Message[ ContentType=" + _contentType + "; Header=" + _header + "; Body=" + _body + " ]";
	}

	/**
	 * Gets the message's body as a String
	 * 
	 * @return
	 */
	public String getBody() {
		return _body;
	}

	private InputStream getBodyAsInputStream() {
		return new ByteArrayInputStream(getBody().getBytes());
	}

	public void setBody(String body) {
		_body = body;
	}

	/**
	 * Returns the message's body as a JDOM Document
	 * 
	 * @return a JDOM tree; <code>null</code> if the message does not contain XML
	 */
	public org.jdom.Document getBodyAsJDOM() {
		org.jdom.Document doc = null;
		try {
			// Parse String
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(new StringReader(_body));
		} catch (Exception e) {
			LOGGER.debug("Could not build JDOM from message body.", e);
		}
		return doc;
	}

	/**
	 * Returns the message body as JMF.
	 * 
	 * @return the JMF message; <code>null</code> if the message does not contain JMF
	 */
	public JDFJMF getBodyAsJMF() {
		JDFJMF jmf = null;
		try {
			if (getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
				jmf = new JDFParser().parseString(_body).getJMFRoot();
			} else if (getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
				Multipart multipart = MimeUtil.getMultiPart(getBodyAsInputStream());
				JDFDoc jdfDoc = MimeUtil.getJDFDoc(multipart.getBodyPart(0));
				if (jdfDoc != null) {
					jmf = jdfDoc.getJMFRoot();
				}
			} else {
				// Try parsing anyway
				jmf = new JDFParser().parseString(_body).getJMFRoot();
			}
		} catch (Exception e) {
			LOGGER.debug("Could not build JMF from message body.", e);
		}
		return jmf;
	}

	/**
	 * Returns the message body as JDF.
	 * 
	 * @return the JDF instance; <code>null</code> if the message does not contain JDF
	 */
	public JDFNode getBodyAsJDF() {
		JDFNode jdf = null;
		try {
			if (getContentType().startsWith(JDFConstants.JDF_CONTENT_TYPE)) {
				jdf = new JDFParser().parseString(_body).getJDFRoot();
			} else if (getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
				Multipart multipart = MimeUtil.getMultiPart(getBodyAsInputStream());
				JDFDoc jdfDoc = MimeUtil.getJDFDoc(multipart.getBodyPart(1));
				if (jdfDoc != null) {
					jdf = jdfDoc.getJDFRoot();
				}
			} else {
				// Try parsing anyway
				jdf = new JDFParser().parseString(_body).getJDFRoot();
			}
		} catch (Exception e) {
			LOGGER.debug("Could not build JDF from message body.", e);
		}
		return jdf;
	}

	/**
	 * Gets the message's header as a String
	 * 
	 * @return
	 */
	public String getHeader() {
		return _header;
	}

	public void setHeader(String header) {
		_header = header;
	}

	public void setContentType(String contentType) {
		_contentType = contentType;
	}

	public String getContentType() {
		return _contentType;
	}

	/**
	 * Adds the results of a Test performed on this Message.
	 * 
	 * @param testResult
	 */
	public void addTestResult(TestResult testResult) {
		LOGGER.debug("Adding TestResult to Message...");
		_testResults.add(testResult);

	}

	/**
	 * Returns a List of TestResults from Tests performed on this Message. The TestResults are order in the order they were added to the Message.
	 * 
	 * @return
	 */
	public List<TestResult> getTestResults() {
		return _testResults;
	}

	public boolean hasPassedAllTests() {
		boolean passed = true;
		for (Iterator<TestResult> i = _testResults.iterator(); i.hasNext();) {
			TestResult result = i.next();
			passed = passed && (result.isPassed() || result.isIgnored());
			if (passed == false) {
				return passed;
			}
		}
		return passed;
	}

	public boolean isSessionInitiator() {
		return _isSessionInitiator;
	}
}
