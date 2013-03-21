/*
 * Created on May 4, 2005
 */
package org.cip4.elk.alces.message;

import java.util.List;

import org.cip4.elk.alces.test.TestResult;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;

/**
 * This class represents a message consisting of two parts, a header and a body.
 * 
 * @author Claes Buckwalter
 * @version $Id$
 */
public interface Message {
	/**
	 * Returns the message's body.
	 * 
	 * @return
	 */
	public String getBody();

	public void setBody(String body);

	/**
	 * Returns the message's body as a JDOM Document
	 * 
	 * @return the message body as a JDOM Document; null if the body could not
	 *         be parsed into a JDOM Document
	 */
	public org.jdom.Document getBodyAsJDOM();

	/**
	 * Returns the message body as JMF.
	 * 
	 * @return the message body as JMF; null if the body could not be parsed
	 *         into JMF
	 */
	public JDFJMF getBodyAsJMF();

	/**
	 * Returns the message body as JDF.
	 * 
	 * @return the message body as JDF; null if the body could not be parsed
	 *         into JDF
	 */
	public JDFNode getBodyAsJDF();

	/**
	 * Gets the message's header as a String
	 * 
	 * @return
	 */
	public String getHeader();

	public void setHeader(String header);

	/**
	 * Sets the message's MIME type.
	 * 
	 * @param contentType
	 *            the MIME type
	 */
	public void setContentType(String contentType);

	/**
	 * Gets the message's MIME type.
	 * 
	 * @return the MIME type
	 */
	public String getContentType();

	/**
	 * Adds the results of a Test performed on this Message.
	 * 
	 * @param testResult
	 */
	public void addTestResult(TestResult testResult);

	/**
	 * Returns a List of TestResults from Tests performed on this Message. The
	 * TestResults are order in the order they were added to the Message.
	 * 
	 * @return
	 */
	public List<TestResult> getTestResults();

	/**
	 * Returns true if all Tests performed on this Message have passed.
	 * 
	 * @return <code>true</code> if all Tests have passed; <code>fale</code>
	 *         otherwise
	 */
	public boolean hasPassedAllTests();

	/**
	 * Returns <code>true</code> if this <code>Message</code> initiated the
	 * <code>TestSession</code> it belongs to.
	 * 
	 * @return <code>true</code> if this message initiated the session;
	 *         <code>false</code> otherwise
	 */
	public boolean isSessionInitiator();
}