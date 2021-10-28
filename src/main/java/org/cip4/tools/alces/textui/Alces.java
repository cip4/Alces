/*
 * Created on May 15, 2005
 */
package org.cip4.tools.alces.textui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.cip4.tools.alces.test.TestRunner;
import org.cip4.tools.alces.test.TestSuite;
import org.cip4.tools.alces.test.TestSuiteImpl;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Alces command line application for running automated tests.
 * 
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class Alces {

	private static Logger log = LoggerFactory.getLogger(Alces.class);

	private static final int PASSED = 0;

	private static final int FAILED = 1;

	private static final int ERROR = -1;

	private static final int TEST_REPORT_ERROR = -2;

	private static final int BAD_PARAMETERS = -3;

	/**
	 * Command line arguments:
	 * <p>
	 * <code>url -s script | testdata [properties]</code>
	 * </p>
	 * <p>
	 * Exit codes:
	 * <ul>
	 * <li>0 All tests passed</li>
	 * <li>1 One or more tests failed</li>
	 * <li>-1 General error</li>
	 * <li>-2 Error displaying test report</li>
	 * <li>-3 Invalid or missing parameters</li>
	 * </ul>
	 * </p>
	 * 
	 * @param args command-line arguments: <code>url -s script | testdata [properties]</code>
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		boolean exit = false;
		if (args.length < 2) {
			System.out.println("'url' and 'testdir' or '-s script' must be specified.");
			System.out.println();
			exit = true;
		} else if (args.length > 4) {
			System.out.println("Too many command line arguments.");
			System.out.println();
			exit = true;
		}
		if (exit) {
			System.out.println("Usage: <url> -s <script> | <testdata> [<properties>]");
			System.out.println("  <url>         URL to send test data to");
			System.out.println("  -s <script>   Path to test script");
			System.out.println("  <testdata>     Path to the file or directory to send");
			System.out.println("  <properties>  Path to a properties file that overrides the default properties file (../conf/alces.properties)");
			System.out.println();
			System.exit(BAD_PARAMETERS);
		}

		// Parse command-line
		String url = args[0];
		File scriptFile;
		File testdata;
		File propsFile;
		int propsArg;
		if (args[1].equals("-s")) {
			// Use script
			scriptFile = new File(args[2]);
			testdata = new File(args[3]);
			propsArg = 4;
		} else {
			// Use old testdata dir
			testdata = new File(args[1]);
			scriptFile = null;
			propsArg = 2;
		}
		// Properties file
		if (args.length > propsArg + 1) {
			propsFile = new File(args[propsArg]);
		} else {
			propsFile = new File(".." + File.separator + "conf" + File.separator + "alces.properties");
		}

		final int returnCode;
		if (scriptFile != null) {
			returnCode = runScript(scriptFile, url, testdata, propsFile);
		} else {
			returnCode = runTestdataScript(url, testdata, propsFile);
		}
		System.exit(returnCode);

	}

	public static int runTestdataScript(String url, File testdata, File propsFile) throws Exception {
		return runScript(new File("alces.js"), url, testdata, propsFile);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public static int runTestdata(File testdataDir, String url, String propsFile) throws Exception {
		TestSuite testSuite = new TestSuiteImpl();
		TestRunner testRunner = new TestRunner(testSuite);
		testRunner.init();
		int returnCode;
		try {
			ConfigurationHandler config = ConfigurationHandler.getInstance();
			config.loadConfiguration(propsFile);
			// Run tests
			testRunner.runTests(url, testdataDir.getAbsolutePath());
			// Serialize test suite
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
			String outputDir = config.getProp(ConfigurationHandler.OUTPUT_DIR) + " " + dateFormat.format(new Date());
			log.info("Writing test suite to '" + outputDir + "'...");
			String outputFile = testRunner.serializeTestSuite(outputDir);
			log.info("Wrote test suite report to: " + outputFile);
			// Display summarized test report
			returnCode = showTextReport(outputFile);
		} catch (Exception e) {
			log.error("An error occured while running the automated tests.", e);
			returnCode = ERROR;
		} finally {
			testRunner.destroy();
		}
		return returnCode;
	}

	public static int runScript(File scriptFile, String url, File testdata, File propsFile) throws Exception {
		// Validate parameters
		if (scriptFile == null) {
			throw new NullPointerException("Script File must not be null");
		} else if (!scriptFile.exists()) {
			throw new FileNotFoundException("Cannot read script File: " + scriptFile.getAbsolutePath());
		} else if (url == null) {
			throw new NullPointerException("JMF URL must not be null");
		} else if (testdata == null) {
			throw new NullPointerException("Testdata File must not be null");
		} else if (!testdata.exists()) {
			throw new FileNotFoundException("Cannot read testdata File: " + testdata.getAbsolutePath());
		} else if (propsFile == null) {
			throw new NullPointerException("Properties path must not be null");
		}

		// Load configuration
		ConfigurationHandler config = ConfigurationHandler.getInstance();
		config.loadConfiguration(propsFile);
		// Create test runner
		TestRunner testRunner = new TestRunner(new TestSuiteImpl(), config);
		testRunner.init();

		// Run script
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			// Wrap and add objects to context
			Object jsTestRunner = Context.javaToJS(testRunner, scope);
			ScriptableObject.putProperty(scope, "testRunner", jsTestRunner);
			Object jsUrl = Context.javaToJS(url, scope);
			ScriptableObject.putProperty(scope, "jmfUrl", jsUrl);
			Object jsTestdata = Context.javaToJS(testdata, scope);
			ScriptableObject.putProperty(scope, "testdata", jsTestdata);
			Object jsConfig = Context.javaToJS(config, scope);
			ScriptableObject.putProperty(scope, "config", jsConfig);
			Object jsLog = Context.javaToJS(log, scope);
			ScriptableObject.putProperty(scope, "log", jsLog);
			// Evaluate script
			Object result = cx.evaluateReader(scope, new FileReader(scriptFile), scriptFile.getName(), 0, null);
			log.info("Scritp result: " + Context.toString(result));
		} finally {
			Context.exit();
		}

		// Serialize test suite
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
		String outputDir = config.getProp(ConfigurationHandler.OUTPUT_DIR) + " " + dateFormat.format(new Date());
		log.info("Writing test suite to '" + outputDir + "'...");
		String outputFile = testRunner.serializeTestSuite(outputDir);
		log.info("Wrote test suite report to: " + outputFile);
		// Display summarized test report
		final int returnCode = showTextReport(outputFile);
		return returnCode;
	}

	/**
	 * Prints the test report to System.out
	 * 
	 * @param outputFile
	 * @return true if all tests passed; otherwise false
	 */
	private static int showTextReport(String outputFile) {
		final int returnCode;
		String xslFile = ConfigurationHandler.getInstance().getProp(ConfigurationHandler.XSLT_REPORT_DIR) + "/report-text.xsl";
		String result = transform(outputFile, xslFile);
		if (result != null) {
			log.debug(result);
			if (result.indexOf("Failed tests:  0") != -1) {
				returnCode = PASSED;
			} else {
				returnCode = FAILED;
			}
		} else {
			log.error("Could not display test results.");
			returnCode = TEST_REPORT_ERROR;
		}
		return returnCode;
	}

	/**
	 * Transforms an XML document using the specified XSL stylesheet.
	 * 
	 * @param xmlPath the absolute path to the XML document to transform
	 * @param xslPath the absolute path to the XSL stylesheet to use
	 */
	private static String transform(String xmlPath, String xslPath) {
		log.debug("Test report: " + xmlPath);
		log.debug("XSL file:    " + xslPath);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(xslPath)));
			Writer stringWriter = new StringWriter();
			transformer.transform(new StreamSource(new File(xmlPath)), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (Exception e) {
			log.error("Could not transform XML test report for display.", e);
		}
		return null;
	}
}
