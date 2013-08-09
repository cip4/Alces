/*
 * Created on Jul 12, 2007
 */
package org.cip4.tools.alces.test.tests;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResultImpl;

/**
 * A <code>TestResult</code> that transforms the test result string using an XSLT transformation.
 * 
 * @author Claes Buckwalter (claes.buckwalter@agfa.com)
 */
public class XsltTestResult extends TestResultImpl {

	private static Logger LOGGER = Logger.getLogger(XsltTestResult.class);

	private final File xslFile;

	private String transformResult = null;

	/**
	 * Creates a TestResult that transforms the result string using an XSLT transformation.
	 * 
	 * @param xslFilePath the path to the XSL file that defines the XSLT transform
	 * @param test
	 * @param message
	 * @param passedTest
	 * @param logMessage
	 */
	public XsltTestResult(String xslFilePath, Test test, Message message, Result result, String logMessage) {
		this(new File(xslFilePath), test, message, result, logMessage);
	}

	/**
	 * Creates a TestResult that transforms the result string using an XSLT transformation.
	 * 
	 * @param xslFile the File object that represents the XSL file that defines the XSLT transform
	 * @param test
	 * @param message
	 * @param passedTest
	 * @param logMessage
	 */
	public XsltTestResult(File xslFile, Test test, Message message, Result result, String logMessage) {
		super(test, message, result, logMessage);
		this.xslFile = xslFile;
	}

	/**
	 * Returns the result string after applying the XSLT transformation. The first time this method is called the transformed result string is cached and
	 * subsequent calls to this method will return the cached result.
	 * 
	 * If the transform cannot be performed the error cause is logged and the original result string is returned instead.
	 * 
	 * @return the transformed result string
	 */
	@Override
	public synchronized String getResultString() {
		String result;
		if (transformResult == null) {
			try {
				LOGGER.debug("Peforming XSLT transform...");
				LOGGER.debug("XSL stylesheet file: " + xslFile.getAbsolutePath());
				Source xslSource = new StreamSource(xslFile);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(xslSource);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("XSLT transform input:\n" + super.getResultString());
				}
				Source xmlSource = new StreamSource(new StringReader(super.getResultString()));
				Writer stringWriter = new StringWriter();
				transformer.transform(xmlSource, new StreamResult(stringWriter));
				transformResult = stringWriter.toString();
				result = transformResult;
			} catch (Exception e) {
				LOGGER.error("Could not perform XSLT transform. Returning transform input instead.", e);
				result = super.getResultString();
			}
		} else {
			result = transformResult;
		}
		return result;
	}
}
