/*
 * Created on May 21, 2006
 */
package org.cip4.tools.alces.test.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.cip4.jdflib.jmf.JDFCommand;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;
import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If the message tested contains a JMF SubmitQueueEntry message, the JDF file referenced by the SubmitQueueEntry message
 * (JMF/Message[@Type='SubmitQueueEntry']/QueueSubmissionParams/@URL) will be validated.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class SubmitQueueEntryTest extends ConfigurableTest {

	/**
	 * The property name for configuring the CheckJDF command-line arguments
	 */
	public final static String CHECKJDF_COMMAND_LINE = "checkjdf.commandline";

	private final static String RES_REPORT_XSL_FILE = "/org/cip4/tools/alces/report/checkjdf.xsl";

	protected final Properties config;
	protected final String commandLine;
	protected final FileSystemManager fileSystemManager;

	/**
	 * Creates a new test and initializes the file system resources required for dereferencing JDF files.
	 * 
	 * @throws IOException if the file system resources could not be initialized
	 */
	public SubmitQueueEntryTest() throws IOException {
		this("SubmitQueueEntryTest - Tests that the JDF files referenced by JMF SubmitQueueEntry Commands are valid using JDFLib-J's CheckJDF.");
	}

	/**
	 * Creates a new test and initializes the file system resources required for dereferencing JDF files.
	 * 
	 * @param description a description of the test
	 * @throws IOException if the file system resources could not be initialized
	 */
	public SubmitQueueEntryTest(String description) throws IOException {
		super(description);
		config = loadConfiguration();
		commandLine = config.getProperty(CHECKJDF_COMMAND_LINE);
		try {
			fileSystemManager = VFS.getManager();
		} catch (FileSystemException fse) {
			log.error("Could not instantiate file system resources.", fse);
			throw fse;
		}
	}

	/**
	 * Tests that the JDF referenced by a JMF SubmitQueueEntry Command is valid. If the Message does not contain a SubmitQueueEntry Command this test is
	 * ignored.
	 * 
	 * @param message a Message
	 * @see SubmitQueueEntryTest
	 */
	@Override
	public TestResult runTest(AbstractJmfMessage message) {
		final StringBuffer testLog = new StringBuffer();
		final TestResult testResult;
		// Test that message is JMF
		if (!message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			testLog.append("Test ignored because message did not contain JMF. Message content-type was: " + message.getContentType());
			testResult = new TestResultImpl(this, message, Result.IGNORED, testLog.toString());
		} else {
			JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
			List<JDFCommand> jmfCommands = getJMFCommands(jmf);
			if (jmfCommands.size() == 0) {
				testResult = new TestResultImpl(this, message, Result.IGNORED, "Test ignored.");
			} else {
				Result result = Result.FAILED;
				for (JDFCommand jmfCommand : jmfCommands) {
					String jdfUrl = getJDFURL(jmfCommand);
					result = Result.getPassed(validateJDF(jdfUrl, testLog));
				}
				InputStream isXslFile = CheckJDFTest.class.getResourceAsStream(RES_REPORT_XSL_FILE);
				testResult = new XsltTestResult(isXslFile, this, message, result, testLog.toString());
			}
		}
		return testResult;
	}

	protected List<JDFCommand> getJMFCommands(JDFJMF jmf) {
		List<JDFCommand> msgs = (Vector) jmf.getMessageVector(EnumFamily.Command, EnumType.SubmitQueueEntry);
		return msgs;
	}

	protected String getJDFURL(JDFCommand jmfCommand) {
		return jmfCommand.getQueueSubmissionParams(0).getURL();
	}

	/**
	 * Reads and validates the JDF that the URL points to. If the JDF can not be found or any other error occurs an error message is written to the test LOGGER.
	 * 
	 * @param jdfUrl the URL to the JDF file
	 * @param testLog the StringBuffer that the test log will be appended to
	 * @return true if the JDF was valid; false otherwise
	 */
	protected boolean validateJDF(String jdfUrl, final StringBuffer testLog) {
		boolean result = false;
		try {
			// Read and validate JDF URL
			FileObject jdfFile = fileSystemManager.resolveFile(jdfUrl);
			InputStream jdfStream = jdfFile.getContent().getInputStream();
			result = CheckJDFWrapper.validateCommandLine(commandLine, jdfStream, testLog);
			jdfFile.close();
		} catch (FileSystemException fse) {
			log.error("The JDF file could not be read.", fse);
			testLog.append("\nThe JDF file could not be read: " + fse.getMessage());
			result = false;
		} catch (IOException ioe) {
			log.error("The JDF file could not be validated.", ioe);
			testLog.append("\nThe JDF file could not be validated: " + ioe.getMessage());
			result = false;
		}
		// Writes test log
		return result;
	}
}
