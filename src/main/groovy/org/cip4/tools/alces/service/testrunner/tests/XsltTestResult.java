/*
 * Created on Jul 12, 2007
 */
package org.cip4.tools.alces.service.testrunner.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>TestResult</code> that transforms the test result string using an XSLT transformation.
 * 
 * @author Claes Buckwalter (claes.buckwalter@agfa.com)
 */
public class XsltTestResult extends TestResult {

	private static Logger log = LoggerFactory.getLogger(XsltTestResult.class);

	private final InputStream isXslFile;

	private String transformResult = null;

	/**
	 * Creates a TestResult that transforms the result string using an XSLT transformation.
	 * 
	 * @param xslFilePath the path to the XSL file that defines the XSLT transform
	 * @param test
	 * @param message
	 * @param logMessage
	 * @throws FileNotFoundException
	 */
	public XsltTestResult(String xslFilePath, Test test, AbstractJmfMessage message, Result result, String logMessage) throws FileNotFoundException {
		this(new FileInputStream(xslFilePath), test, message, result, logMessage);
	}

	/**
	 * Creates a TestResult that transforms the result string using an XSLT transformation.
	 */
	public XsltTestResult(InputStream isXslFile, Test test, AbstractJmfMessage message, Result result, String logMessage) {
		super(test, message, result, logMessage);
		this.isXslFile = isXslFile;
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
				log.debug("Peforming XSLT transform...");
				// LOGGER.debug("XSL stylesheet file: " + xslFile.getAbsolutePath());
				Source xslSource = new StreamSource(isXslFile);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(xslSource);
				if (log.isDebugEnabled()) {
					log.debug("XSLT transform input:\n" + super.getResultString());
				}
				Source xmlSource = new StreamSource(new StringReader(super.getResultString()));
				Writer stringWriter = new StringWriter();
				transformer.transform(xmlSource, new StreamResult(stringWriter));
				transformResult = stringWriter.toString();
				result = transformResult;
			} catch (Exception e) {
				log.error("Could not perform XSLT transform. Returning transform input instead.", e);
				result = super.getResultString();
			}
		} else {
			result = transformResult;
		}
		return result;
	}
}
