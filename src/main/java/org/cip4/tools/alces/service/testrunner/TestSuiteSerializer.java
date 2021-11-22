/*
 * Created on May 15, 2005
 */
package org.cip4.tools.alces.service.testrunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.model.TestSession;
import org.cip4.tools.alces.service.testrunner.model.TestSuite;
import org.cip4.tools.alces.util.AlcesPathUtil;
import org.cip4.tools.alces.util.JDFConstants;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes a TestSuite. The TestSuite XML file and the resources related to it are written to the specified base directory. The TestSuite XML file is named
 * <code>suite.xml</code>.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestSuiteSerializer {

	private static Logger log = LoggerFactory.getLogger(TestSuiteSerializer.class);

	private DateFormat _dateFormat = null;

	private final String _reportResourceDir;

	private final static String REPORT_DIR = AlcesPathUtil.ALCES_CONFIG_DIR;

	private final static String RES_REPORT_ROOT = "/org/cip4/tools/alces/report/";

	public TestSuiteSerializer() {
		this(REPORT_DIR);
	}

	/**
	 * Constructor that allows the directory where the XSL and CSS files for viewing the test report are stored.
	 * 
	 * @param reportResourceDir the directory containing the XSL and CSS files used to view the test report
	 */
	private TestSuiteSerializer(String reportResourceDir) {
		_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		if (reportResourceDir == null) {
			throw new IllegalArgumentException("The report directory must be configured.");
		}
		_reportResourceDir = reportResourceDir;
	}

	/**
	 * Serializes a TestSuite writing it to the specified base output directory. If the base output directory does not exist it is created.
	 * 
	 * @param suite the TestSuite to serialize
	 * @return the XML file the TestSuite was serialized to
	 * @throws IOException
	 */
	public String serialize(TestSuite suite, String outputDirPath) throws IOException {
		// Creates output directory
		File outputDir = new File(outputDirPath);
		outputDir.mkdir();
		// Creates output file
		File outFile = new File(outputDir, "report.xml");
		log.debug("Serializing TestSuite to '" + outFile.getAbsolutePath() + "'...");
		Writer out = new BufferedWriter(new FileWriter(outFile));
		// Serializes TestSuite
		out.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		out.write("<?xml-stylesheet type='text/xsl' href='");
		out.write("resources/report-html.xsl");
		out.write("'?>");
		// out.write("<!DOCTYPE suite SYSTEM 'resources/report.dtd'>");
		serializeTestSuite(suite, outputDir, out);
		out.close();
		// Copy stylesheet and images
		copyReportResources(outputDir.getAbsolutePath());
		log.debug("Serialized TestSuite.");
		return outFile.getAbsolutePath();
	}

	private void copyReportResources(String reportDir) throws IOException {

		// create list of all report resources
		List<String> reportResources = new ArrayList<String>(25);
		reportResources.add("checkjdf.css");
		reportResources.add("checkjdf.xsl");
		reportResources.add("report.css");
		reportResources.add("report.dtd");
		reportResources.add("report-html.xsl");
		reportResources.add("report-text.xsl");
		reportResources.add("images/jmf.gif");
		reportResources.add("images/message_in_fail.gif");
		reportResources.add("images/message_in_pass.gif");
		reportResources.add("images/message_in.gif");
		reportResources.add("images/message_out_fail.gif");
		reportResources.add("images/message_out_pass.gif");
		reportResources.add("images/message_out.gif");
		reportResources.add("images/session_fail.gif");
		reportResources.add("images/session_pass.gif");
		reportResources.add("images/session_pass-new.gif");
		reportResources.add("images/session.gif");
		reportResources.add("images/test_fail.gif");
		reportResources.add("images/test_pass.gif");

		// copy to output directory
		for (String path : reportResources) {
			String target = FilenameUtils.concat(reportDir, "resources");
			target = FilenameUtils.concat(target, path);

			new File(FilenameUtils.getFullPath(target)).mkdirs();
			FileOutputStream fos = new FileOutputStream(target);
			InputStream is = TestSuiteSerializer.class.getResourceAsStream(RES_REPORT_ROOT + path);
			IOUtils.copy(is, fos);
			fos.close();
			is.close();
		}
	}

	/**
	 * Serializes a TestSuite
	 * 
	 * @param outputDir the output directory to write referenced files to
	 * @param out the Writer used to append to the TestSuites's log file
	 * @throws IOException
	 */
	private void serializeTestSuite(TestSuite suite, File outputDir, Writer out) throws IOException {
		out.write("<suite timestamp='");
		out.write(_dateFormat.format(new Date()));
		out.write("'>");
		for (TestSession testSession : suite.getTestSessions()) {
			serializeTestSession(testSession, outputDir, out);
		}
		out.write("</suite>");
	}

	private void serializeTestSession(TestSession testSession, File outputDir, Writer out) throws IOException {
		out.write("<session url='");
		out.write(testSession.getTargetUrl());
		out.write("'>\n");
		AbstractJmfMessage message = testSession.getInitializingJmfMessage();
		serializeMessage(message, outputDir, out);
		out.write("</session>\n");
	}

	private void serializeMessage(AbstractJmfMessage message, File outputDir, Writer out) throws IOException {
		log.debug("Serializing message...");
		out.write("<message type='");
		if (message instanceof OutgoingJmfMessage) {
			out.write("out");
		} else if (message instanceof IncomingJmfMessage) {
			out.write("in");
		}
		out.write("'>\n");
		out.write("<header>\n");
		out.write("<![CDATA[");
		if (message.getHeader() != null) {
			out.write(message.getHeader());
		}
		out.write("]]>\n");
		out.write("</header>\n");
		out.write("<body url='");
		String outputUrl = null;
		try {
			outputUrl = saveMessageBody(message, outputDir);
		} catch (IOException ioe) {
			outputUrl = "FAILED TO SAVE MESSAGE";
		}
		out.write(outputUrl);
		out.write("'>\n");
		if (!(message.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE) || message.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE))) {
			out.write("<![CDATA[");
			out.write(message.getBody());
			out.write("]]>\n");
		}
		out.write("</body>");
		// TODO Serialize Preprocessors
		out.write("<tests>");
		for (TestResult result : message.getTestResults()) {
			serializeTestResult(result, outputDir, out);
		}
		out.write("</tests>\n");
		List<? extends AbstractJmfMessage> messages = null;

		if (message instanceof OutgoingJmfMessage) {
			messages = ((OutgoingJmfMessage) message).getIncomingJmfMessages();

		} else if (message instanceof IncomingJmfMessage) {
			messages = ((IncomingJmfMessage) message).getOutgoingJmfMessages();

		}
		if (message != null && messages.size() != 0) {
			out.write("<messages>\n");
			for (AbstractJmfMessage subMessage : messages) {
				serializeMessage(subMessage, outputDir, out);
			}
			out.write("</messages>\n");
		}
		out.write("</message>");
		log.debug("Serialized message.");
	}

	private void serializeTestResult(TestResult result, File outputDir, Writer out) throws IOException {
		log.debug("Serializing " + result.getTest().getClass() + " TestResult...");
		out.write("<test passed='");
		out.write(Boolean.toString(result.getResult() == TestResult.Result.PASSED));
		out.write("' description='");
		out.write(StringEscapeUtils.escapeXml(result.getTest().getDescription()));
		out.write("' type='");
		out.write(result.getTest().getClass().getName());
		out.write("'>\n");
		out.write("<log>\n");
		if (result.getResultString() != null) {
			out.write(StringEscapeUtils.escapeXml(result.getResultString()));
		}
		out.write("\n");
		out.write("</log>\n");
		out.write("</test>\n");
		log.debug("Serialized " + result.getTest().getClass() + " TestResult.");
	}

	/**
	 * Saves a messages body to a directory
	 * 
	 * @param message
	 * @param outputDir
	 * @return a URL to the file containing the message body
	 */
	private String saveMessageBody(AbstractJmfMessage message, File outputDir) throws IOException {
		final String filename;
		final String extension;

		if (message.getContentType().startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
			JDFJMF jmf = JmfUtil.getBodyAsJMF(message);
			if (jmf != null) {
				JDFMessage msg = jmf.getMessageElement(null, null, 0);
				filename = normalize(jmf.getTimeStamp().getDateTimeISO()) + "_" + msg.getTagName() + msg.getType() + "_" + msg.getID() + "_" + RandomStringUtils.randomAlphanumeric(6);
			} else {
				filename = RandomStringUtils.randomAlphanumeric(16);
			}
			extension = JDFConstants.JMF_EXTENSION;
		} else if (message.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
			filename = RandomStringUtils.randomAlphanumeric(16);
			extension = JDFConstants.JMF_MIME_EXTENSION;
		} else if (message.getContentType().startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
			filename = RandomStringUtils.randomAlphanumeric(16);
			extension = JDFConstants.JDF_MIME_EXTENSION;
		} else {
			filename = RandomStringUtils.randomAlphanumeric(16);
			extension = ".txt";
		}
		File messageFile = new File(outputDir, filename + extension);
		log.debug("Writing message body to file '" + messageFile.getAbsolutePath() + "'...");
		IOUtils.write(message.getBody(), new FileOutputStream(messageFile));
		return "file:" + messageFile.getAbsolutePath();
	}

	private String normalize(String filename) {
		return filename.replaceAll("[^a-zA-Z 0-9]", "");
	}
}
