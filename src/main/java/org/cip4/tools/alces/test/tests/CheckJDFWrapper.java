/*
 * Created on Jun 21, 2005
 */
package org.cip4.tools.alces.test.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.cip4.jdflib.core.JDFElement.EnumValidationLevel;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.core.XMLDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.tools.alces.util.AlcesPathUtil;
import org.cip4.tools.alces.util.JDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for easy validation with CheckJDF.
 * 
 * TODO Use caching to improve performance
 * 
 * @author Claes Buckwalter
 */
public class CheckJDFWrapper {

	private static Logger log = LoggerFactory.getLogger(CheckJDFWrapper.class);

	/**
	 * Prevents instances from being created.
	 */
	private CheckJDFWrapper() {
	}

	/**
	 * Validates the JDF instance or JMF message read from the <code>InputStream</code>
	 * 
	 * @param jdfIn the JDF instance or JMF message to parse
	 * @param reportFile the absolute path to the file to write the validation report to
	 * @return true if the JDF was valid; false otherwise
	 * @throws IOException
	 */
	public static boolean validate(InputStream jdfIn, File reportFile) throws IOException {
		// Write input stream to temp file
		File tempJdf = createTempFile(".jdf");
		try {
			FileOutputStream jdfOut = new FileOutputStream(tempJdf);
			IOUtils.copy(jdfIn, jdfOut);
			return validate(tempJdf, new File(AlcesPathUtil.ALCES_TEST_DATA_DIR + "/schema/JDF.xsd"), null, reportFile);

		} catch (IOException ioe) {
			System.err.println(ioe);
			return false;
		} finally {
			tempJdf.delete();
		}
	}

	/**
	 * Validates a JDF instance or JMF message read from the specified <code>String</code>
	 * 
	 * @param jdf the JDF instance or JMF message to parse
	 * @return true if valid; false otherwise
	 * @throws IOException
	 */
	public static boolean validate(String jdf) throws IOException {
		File tempReport = createTempFile(".xml");
		try {
			return validate(jdf, tempReport);
		} finally {
			tempReport.delete();
		}
	}

	/**
	 * Validates a JDF instance or JMF message read from the specified <code>String</code>
	 * 
	 * @param jdf the JDF instance or JMF message to parse
	 * @param report a buffer to append the XML validation report to
	 * @return true if valid; false otherwise
	 */
	public static boolean validate(String jdf, final StringBuffer report) throws IOException {
		File tempReport = createTempFile(".xml");

		try {
			boolean result = validate(jdf, tempReport);
			report.append(IOUtils.toString(new FileInputStream(tempReport)));
			return result;
		} finally {
			tempReport.delete();
		}
	}

	public static boolean validate(InputStream jdf, final StringBuffer report) throws IOException {
		File tempReport = createTempFile(".xml");
		try {
			boolean result = validate(jdf, tempReport);
			report.append(IOUtils.toString(new FileInputStream(tempReport)));
			return result;
		} finally {
			tempReport.delete();
		}
	}

	public static boolean validate(File jdf, final StringBuffer report) throws IOException {
		File tempReport = createTempFile(".xml");
		try {
			boolean result = validate(jdf, tempReport);
			report.append(IOUtils.toString(new FileInputStream(tempReport)));
			return result;
		} finally {
			tempReport.delete();
		}
	}

	/**
	 * Validates the JDF instance or JMF message read from the <code>String</code>
	 * 
	 * @param jdf the JDF instance or JMF message to parse
	 * @param reportFile the absolute path to the file to write the validation report to
	 * @throws IOException
	 */
	public static boolean validate(String jdf, File reportFile) throws IOException {
		// Write input stream to temp file
		File tempJdf = createTempFile(".jdf");
		try {
			FileOutputStream jdfOut = new FileOutputStream(tempJdf);
			IOUtils.write(jdf, jdfOut);
			return validate(tempJdf, new File(AlcesPathUtil.ALCES_TEST_DATA_DIR + "/schema/JDF.xsd"), null, reportFile);
		} catch (IOException ioe) {
			System.err.println(ioe);
			return false;
		} finally {
			tempJdf.delete();
		}
	}

	/**
	 * Validates the JDF instance or JMF message read from a file. A validation report in XML format is written to a file. Both schema valid
	 * 
	 * @param jdfFile the JDF instance or JMF message to parse
	 * @param reportFile the absolute path to the file to write the validation report to
	 * @throws IOException
	 */
	public static boolean validate(File jdfFile, File reportFile) throws IOException {
		return validate(jdfFile, new File(AlcesPathUtil.ALCES_TEST_DATA_DIR + "/schema/JDF.xsd"), null, reportFile);
	}

	/**
	 * Validates the JDF instance or JMF message read from a file. A validation report in XML format is written to a file.
	 * 
	 * @param jdf the JDF instance or JMF message to parse
	 * @param schemaFile The schema file to use for schema validation. If <code>null</code> then schema validation is not performed.
	 * @param devcap The device capabilities to use for validation. If <code>null</code> testing against device capabilities is not performed.
	 * @param reportFile The file to write the validation XMl report to. If <code>null</code> no XML report is written.
	 */
	public static boolean validate(JDFNode jdf, File schemaFile, JDFJMF devcap, File reportFile) throws IOException {
		if (jdf == null) {
			throw new IllegalArgumentException("JDFNode may not be null");
		}
		File jdfFile = createTempFile(".jdf");
		IOUtils.write(jdf.toXML(), new FileOutputStream(jdfFile));
		File devcapFile = null;
		if (devcap != null) {
			devcapFile = createTempFile(".xml");
			IOUtils.write(devcap.toXML(), new FileOutputStream(devcapFile));
		}
		return validate(jdfFile, schemaFile, devcapFile, reportFile);
	}

	/**
	 * Validates the JDF instance or JMF message read from a file. A validation report in XML format is written to a file.
	 * 
	 * @param jdfFile the JDF instance or JMF message to parse
	 * @param schemaFile The schema file to use for schema validation. If <code>null</code> then schema validation is not performed.
	 * @param devcapFile The device capabilities file to use for validation. If <code>null</code> testing against device capabilities is not performed.
	 * @param reportFile The file to write the validation XML report to. If <code>null</code> no XML report is written.
	 * @return <code>true</code> if JDF file passed validatio; <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean validate(File jdfFile, File schemaFile, File devcapFile, File reportFile) throws IOException {
		// JDF
		if (jdfFile == null) {
			throw new IllegalArgumentException("The JDF File may not be null");
		}
		// Configure
		CheckJDF checkJDF = new CheckJDF();
		checkJDF.setPrint(false);
		checkJDF.bQuiet = true;
		checkJDF.setIgnorePrivate(true);
		checkJDF.level = EnumValidationLevel.Complete;
		checkJDF.bTiming = false;
		checkJDF.bValidate = true;
		if (devcapFile != null) {
			checkJDF.devCapFile = devcapFile.getCanonicalPath();
		}
		if (schemaFile != null) {
			checkJDF.setJDFSchemaLocation(schemaFile);
			// XXX
			// checkJDF.setJDFSchemaLocation(schemaFile.toURI().toURL().toExternalForm());
		}
		// Validate
		XMLDoc reportDoc = checkJDF.processSingleFile(jdfFile.getCanonicalPath());
		// Write report to file
		// TODO Only write report file when necessary
		if (reportFile != null) {
			reportDoc.write2File(reportFile.getCanonicalPath(), 3, true);
		}

		String schemaResult = reportDoc.getRoot().getXPathAttribute("/CheckOutput/TestFile/SchemaValidationOutput/@ValidationResult", "false");
		String properResult = reportDoc.getRoot().getXPathAttribute("/CheckOutput/TestFile/CheckJDFOutput/@IsValid", "true");
		return (schemaResult.equals("Valid") || schemaResult.equals("NotPerformed")) && properResult.equals("true");
	}

	/**
	 * Calls CheckJDF using the specified command line.
	 * 
	 * @param commandLineArgs
	 * @deprecated CheckJDF's validation settings can now relatively easily be configured by setting public members instead of using the command line.
	 * @see #validate(File, File, File, File)
	 */
	@Deprecated
	public static void validateCommandLine(String[] commandLineArgs) {
		CheckJDF checker = new CheckJDF();
		checker.setPrint(false);
		checker.validate(commandLineArgs, null);
		checker = null;
	}

	/**
	 * Calls JDFLib-J's <i>CheckJDF</i> using the specified command-line arguments. Before calling CheckJDF two temporary files are created: one to which the
	 * input JDF data will be written; and one to which CheckJDF's XML test report will be written. See {@link #validateCommandLine(String, File, File)} for
	 * details.
	 * 
	 * @param commandLine the command line
	 * @param jdfData a <code>String</code> containing the JDF instance to validate
	 * @param report a <code>StringBuffer</code> to which CheckJDF's XML test report will be appended
	 * @return <code>true</code> if the JDF was valid; <code>false</code> otherwise
	 * @throws IOException if the temporary files could not be written/read
	 */
	public static boolean validateCommandLine(String commandLine, String contentType, String jdfData, StringBuffer report) throws IOException {
		// Create temp files
		File tempReport = createTempFile(".xml");
		File tempContent;
		if (JDFConstants.JDF_CONTENT_TYPE.equals(contentType)) {
			tempContent = createTempFile(JDFConstants.JDF_EXTENSION);
		} else if (JDFConstants.JMF_CONTENT_TYPE.equals(contentType)) {
			tempContent = createTempFile(JDFConstants.JMF_EXTENSION);
		} else if (JDFConstants.MIME_CONTENT_TYPE.equals(contentType)) {
			tempContent = createTempFile(JDFConstants.JMF_MIME_EXTENSION);
		} else {
			// Assume JDF anyway
			tempContent = createTempFile(JDFConstants.JDF_EXTENSION);
		}
		FileOutputStream jdfOut = new FileOutputStream(tempContent);
		IOUtils.write(jdfData, jdfOut);
		// Call CheckJDF
		final boolean result = validateCommandLine(commandLine, tempContent, tempReport);
		// Read report
		report.append(IOUtils.toString(new FileInputStream(tempReport)));
		return result;
	}

	/**
	 * Calls JDFLib-J's <i>CheckJDF</i> with the specified command-line arguments. Before calling CheckJDF two temporary files are created: one to which the
	 * input JDF data will be written; and one to which CheckJDF's XML test report will be written. See {@link #validateCommandLine(String, File, File)} for
	 * details.
	 * 
	 * @param commandLine the command-line arguments for CheckJDF
	 * @param jdfStream an <code>InputStream</code> from the JDF instance to validate
	 * @param report a <code>StringBuffer</code> to which CheckJDF's XML report is appended
	 * @return <code>true</code> if the JDF instance was valid; <code>false</code> otherwise
	 * @throws IOException
	 * @see {@link #validateCommandLine(String, String, StringBuffer)}
	 */
	public static boolean validateCommandLine(String commandLine, InputStream jdfStream, StringBuffer report) throws IOException, IOException {
		File tempReport = createTempFile(".xml");
		final boolean result = validateCommandLine(commandLine, jdfStream, tempReport);
		report.append(IOUtils.toString(new FileInputStream(tempReport)));
		return result;
	}

	/**
	 * Calls JDFLib-J's <i>CheckJDF</i> with the specified command-line arguments. Before calling CheckJDF a temporary file is created to which the input JDF
	 * data will be written. See {@link #validateCommandLine(String, File, File)} for details.
	 * 
	 * @param commandLine the command-line arguments for CheckJDF
	 * @param jdfStream an <code>InputStream</code> from the JDF instance to validate
	 * @param reportFile the file to which CheckJDF's XML report will be written
	 * @return <code>true</code> if the JDF instance was valid; <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean validateCommandLine(String commandLine, InputStream jdfStream, File reportFile) throws IOException {
		File tempJdf = createTempFile(".jdf");
		FileOutputStream jdfOut = new FileOutputStream(tempJdf);
		IOUtils.copy(jdfStream, jdfOut);
		return validateCommandLine(commandLine, tempJdf, reportFile);
	}

	/**
	 * Calls JDFLib-J's <i>CheckJDF</i> with the specified command-line arguments. The following subsitution variables can be used to insert the paths of the
	 * JDF file and the report file in the command-line arguments:
	 * <ul>
	 * <li><code>$J</code> - will be replaced by the absolute path to the JDF file</li>
	 * <li><code>$R</code> - will be replaced by the absolute path to the XML report file</li>
	 * </ul>
	 * 
	 * @param commandLine the command-line arguments for CheckJDF
	 * @param jdfFile the JDF file to validate
	 * @param reportFile the file to which CheckJDF's XML report will be written
	 * @return <code>true</code> if the JDF instance was valid; <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean validateCommandLine(String commandLine, File jdfFile, File reportFile) {
		// Normalize temp file paths
		String tempReportPath = reportFile.getAbsolutePath().replaceAll("\\\\", "/");
		String tempJdfPath = jdfFile.getAbsolutePath().replaceAll("\\\\", "/");
		// Replace variables in command-line arguments
		commandLine = commandLine.replaceAll("\\$J", tempJdfPath);
		commandLine = commandLine.replaceAll("\\$R", tempReportPath);
		// Convert command-line string to array
		StringTokenizer t = new StringTokenizer(commandLine, " ");
		String[] commandLineArgs = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreTokens()) {
			commandLineArgs[i] = t.nextToken();
			i++;
		}
		// Call CheckJDF
		log.debug("Validating command line: " + commandLine);
		CheckJDF.main(commandLineArgs);
		// Read result
		XMLDoc reportDoc = new JDFParser().parseFile(reportFile.getAbsolutePath());
		String schemaResult = reportDoc.getRoot().getXPathAttribute("/CheckOutput/TestFile/SchemaValidationOutput/@ValidationResult", "false");
		String properResult = reportDoc.getRoot().getXPathAttribute("/CheckOutput/TestFile/CheckJDFOutput/@IsValid", "true");
		return (schemaResult.equals("Valid") || schemaResult.equals("NotPerformed")) && properResult.equals("true");

	}

	/**
	 * Creates a temp file with a random 16 character name, excluding a suffix.
	 * 
	 * @param suffix the file suffix of the temp file
	 * @return the temp file
	 * @throws IOException
	 */
	private static File createTempFile(String suffix) throws IOException {
		// Write input stream to temp file
		String fileName = RandomStringUtils.randomAlphanumeric(16);
		File tempFile = File.createTempFile(fileName, suffix);
		tempFile.deleteOnExit();
		return tempFile;
	}
}
