/*
 * Created on Apr 23, 2005
 */
package org.cip4.tools.alces.test.tests;

import java.io.File;
import java.util.Properties;

import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.test.TestResult.Result;

/**
 * Uses CheckJDF to test if a <code>Message</code> contains a valid JDF
 * instance or a JMF message.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class CheckJDFTest extends ConfigurableTest {

	public final static String CHECKJDF_COMMAND_LINE = "checkjdf.commandline";
	
	public final static File REPORT_XSL_FILE = new File("../conf/report/checkjdf.xsl");

	private final Properties config;

	private final String commandLine;

	public CheckJDFTest() {
		this("CheckJDFTest - Tests that JMF messages are valid using JDFLib-J's CheckJDF.");
	}

	public CheckJDFTest(String description) {
		super(description);
		config = loadConfiguration();
		commandLine = config.getProperty(CHECKJDF_COMMAND_LINE);
	}

	/**
	 * Validates a <code>Message</code> containing a JDF instance or a JMF
	 * message. Messages that are not of the JDF or JMF content-types will be
	 * ignored.
	 * 
	 * @param message
	 *            the message to validate
	 */
	@Override
	public TestResult runTest(Message message) {
		TestResult result;		
		StringBuffer logMsg = new StringBuffer();
		try {
			// Validate
			StringBuffer xmlReport = new StringBuffer();
			boolean passedTest = CheckJDFWrapper.validateCommandLine(commandLine, message.getContentType(), message.getBody(),
					xmlReport);
			logMsg.append(xmlReport);
			result = new XsltTestResult(REPORT_XSL_FILE, this, message, Result.getPassed(passedTest), logMsg.toString());			
		} catch (Exception e) {
			logMsg.append("An error occurred while validating the message body. Maybe the"
					+ " message body does not contain a JDF instance or a JMF message?");
			log.error("Could not validate message: ", e);
			result = new TestResultImpl(this, message, Result.FAILED, logMsg.toString());
		} 
		return result;
	}
}
