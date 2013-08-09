/*
 * Created on Jul 20, 2005
 */
package org.cip4.tools.alces.test.tests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.JDFFileFilter;
import org.cip4.tools.alces.util.JMFFileFilter;

/**
 * Requires that 'user.dir' is set to 'Alces/src/bin' for CheckJDFWrapper's path to the JDF schema to be correct.
 * 
 * TODO Enable JDF tests.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class CheckJDFWrapperTest extends AlcesTestCase {

	private File[] _jdfFiles;

	private File[] _jmfFiles;

	private static final String TEST_DATA = initTestData();

	private static String initTestData() {

		String resPath = "/org/cip4/tools/alces/data/CheckJDFWrapperTest/";
		String s = CheckJDFWrapperTest.class.getResource(resPath).getFile();

		return s;
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		/*
		 * String userDir = System.getProperty("user.dir"); if (!userDir.endsWith("/src/bin")) { userDir = System.setProperty("user.dir", userDir + "/src/bin");
		 * }
		 */

		File jdfDir = new File(TEST_DATA + "jdf/").getCanonicalFile();
		assertTrue("JDF dir does not exist: " + jdfDir.getAbsolutePath(), jdfDir.exists());
		_jdfFiles = jdfDir.listFiles(new TestFileFilter(new JDFFileFilter()));
		assertTrue("No JDF files in:" + jdfDir, _jdfFiles.length > 0);
		File jmfDir = new File(TEST_DATA + "jmf/").getCanonicalFile();
		assertTrue("JMF dir does not exist: " + jmfDir.getAbsolutePath(), jmfDir.exists());
		_jmfFiles = jmfDir.listFiles(new TestFileFilter(new JMFFileFilter()));
		assertTrue("No JMF files in:" + jmfDir, _jmfFiles.length > 0);
	}

	/*
	 * Class under test for void validate(InputStream, File)
	 */
	// public void testValidateInputStreamFile_JDF() throws Exception {
	// File reportFile = File.createTempFile("validation_report", ".xml");
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// boolean result = CheckJDFWrapper.validate(new
	// FileInputStream(_jdfFiles[i]),
	// reportFile);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }
	// }
	public void testValidateInputStreamFile_JMF() throws Exception {
		File reportFile = File.createTempFile("validation_report", ".xml"); // Create
		// a
		// temp
		// file
		reportFile.delete();
		for (int i = 0; i < _jmfFiles.length; i++) {
			boolean result = CheckJDFWrapper.validate(new FileInputStream(_jmfFiles[i]), reportFile);
			assertTrue("Validation failed: " + i + ". " + _jmfFiles[i], result);
			assertTrue("Report missing for JMF file " + i + ": " + _jmfFiles[i], reportFile.exists());
			reportFile.delete();
		}
	}

	public void testValidateInputStreamFile_JMFNull() throws Exception {
		StringBuffer resultLog = new StringBuffer();
		for (int i = 0; i < _jmfFiles.length; i++) {
			boolean result = CheckJDFWrapper.validate(new FileInputStream(_jmfFiles[i]), resultLog);
			assertTrue("Validation failed: " + i + ". " + _jmfFiles[i], result);
		}
	}

	/*
	 * Class under test for String validate(String)
	 */
	// public void testValidateString_JDF() throws Exception {
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// String data = IOUtils.toString(new FileInputStream(_jdfFiles[i]));
	// boolean result = CheckJDFWrapper.validate(data);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// System.out.println(result);
	// }
	// }
	public void testValidateString_JMF() throws Exception {
		for (int i = 0; i < _jmfFiles.length; i++) {
			String data = IOUtils.toString(new FileInputStream(_jmfFiles[i]));
			boolean result = CheckJDFWrapper.validate(data);
			assertTrue("Validation failed: " + i + ". " + _jmfFiles[i], result);
		}
	}

	public void testValidateStringStringBuffer_JMF() throws Exception {
		String data = IOUtils.toString(new FileInputStream(_jmfFiles[0]));
		StringBuffer report = new StringBuffer();
		boolean result = CheckJDFWrapper.validate(data, report);
		assertNotNull("Report is null", report);
		assertFalse("Report is empty", report.length() == 0);
		assertTrue("Validation failed: " + _jmfFiles[0], result);

	}

	/*
	 * Class under test for void validate(String, File)
	 */
	// public void testValidateStringFile_JDF() throws Exception {
	// File reportFile = File.createTempFile("validation_report", ".xml");
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// String data = IOUtils.toString(new FileInputStream(_jdfFiles[i]));
	// boolean result = CheckJDFWrapper.validate(data, reportFile);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }
	// for (int i = 0; i < _jmfFiles.length; i++) {
	// String data = IOUtils.toString(new FileInputStream(_jmfFiles[i]));
	// boolean result = CheckJDFWrapper.validate(data, reportFile);
	// assertTrue("Validation failed: " + _jmfFiles[i], result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }
	// }
	public void testValidateStringFile_JMF() throws Exception {
		File reportFile = File.createTempFile("validation_report", ".xml");
		reportFile.delete();
		for (int i = 0; i < _jmfFiles.length; i++) {
			String data = IOUtils.toString(new FileInputStream(_jmfFiles[i]));
			boolean result = CheckJDFWrapper.validate(data, reportFile);
			assertTrue("Validation failed: " + i + ". " + _jmfFiles[i], result);
			assertTrue(reportFile.exists());
			reportFile.delete();
		}
	}

	/*
	 * Class under test for void validate(File, File)
	 */
	// public void testValidateFileFile_JDF() throws Exception {
	// File reportFile = File.createTempFile("validation_report", ".xml");
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// boolean result = CheckJDFWrapper.validate(_jdfFiles[i], reportFile);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }
	// }
	public void testValidateFileFile_JMF() throws Exception {
		File reportFile = File.createTempFile("validation_report", ".xml");
		reportFile.delete();
		for (int i = 0; i < _jmfFiles.length; i++) {
			boolean result = CheckJDFWrapper.validate(_jmfFiles[i], reportFile);
			assertTrue("Validation failed: " + i + ". " + _jmfFiles[i], result);
			assertTrue(reportFile.exists());
			reportFile.delete();
		}
	}

	// public void testValidateFileFileFileFile_JDF() throws Exception {
	// File schemaFile = new File(TEST_DATA +
	// "schema/JDF.xsd").getCanonicalFile();
	// assertTrue(schemaFile.exists());
	// File devcapFile = new File(TEST_DATA +
	// "devcaps/DevCaps_LayCrImp.jdf").getCanonicalFile();
	// assertTrue(devcapFile.exists());
	// File reportFile = File.createTempFile("report", ".xml");
	//
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// boolean result = CheckJDFWrapper.validate(_jdfFiles[i], schemaFile,
	// devcapFile, reportFile);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }
	// }

	/**
	 * Disabled until a DevCaps file for JMF has been written.
	 */
	public void disabled_testValidateFileFileFileFile_JMF() throws Exception {
		File schemaFile = new File(TEST_DATA + "schema/JDF.xsd").getCanonicalFile();
		assertTrue(schemaFile.exists());
		File devcapFile = new File(TEST_DATA + "devcaps/DevCaps_LayCrImp.jdf").getCanonicalFile();
		assertTrue(devcapFile.exists());
		File reportFile = File.createTempFile("report", ".xml");
		reportFile.delete();
		for (int i = 0; i < _jmfFiles.length; i++) {
			boolean result = CheckJDFWrapper.validate(_jmfFiles[i], schemaFile, devcapFile, reportFile);
			assertTrue("Validation failed: " + _jmfFiles[i], result);
			assertTrue(reportFile.exists());
			reportFile.delete();
		}
	}

	// public void testValidateFileFileNullFileNullFile_JDF() throws Exception {
	// File schemaFile = null;
	// File devcapFile = null;
	// File reportFile = null;
	// for (int i = 0; i < _jdfFiles.length; i++) {
	// boolean result = CheckJDFWrapper.validate(_jdfFiles[i], schemaFile,
	// devcapFile, reportFile);
	// assertTrue("Validation failed: " + _jdfFiles[i], result);
	// }
	// }

	public void testValidateFileFileNullFileNullFile_JMF() throws Exception {
		File schemaFile = null;
		File devcapFile = null;
		File reportFile = null;
		for (int i = 0; i < _jmfFiles.length; i++) {
			boolean result = CheckJDFWrapper.validate(_jmfFiles[i], schemaFile, devcapFile, reportFile);
			assertTrue(result);
		}
	}

	// public void testValidateJDFNodeFileJDFDeviceCapFile() throws Exception {
	// File jdfFile = new File(TEST_DATA +
	// "jdf/MISPrepress-ICS-Minimal.jdf").getCanonicalFile();
	// assertTrue(jdfFile.exists());
	// JDFNode jdf = new JDFParser().parseStream(new
	// FileInputStream(jdfFile)).getJDFRoot();
	// assertNotNull(jdf);
	// File schemaFile = new File(TEST_DATA +
	// "schema/JDF.xsd").getCanonicalFile();
	// assertTrue(schemaFile.exists());
	// File devCapFile = new File(TEST_DATA +
	// "devcaps/DevCaps_LayCrImp.jdf").getCanonicalFile();
	// assertTrue(devCapFile.exists());
	// JDFJMF devCap = new JDFParser().parseStream(new
	// FileInputStream(devCapFile)).getJMFRoot();
	// // NodeList devCapNodes = doc.getElementsByTagName("DeviceCap");
	// // JDFDeviceCap devCap = (JDFDeviceCap) devCapNodes.item(0);
	// assertNotNull(devCap);
	// File reportFile = File.createTempFile("report", ".xml");
	// boolean result = CheckJDFWrapper.validate(jdf, schemaFile, devCap,
	// reportFile);
	// assertTrue(result);
	// assertTrue(reportFile.exists());
	// reportFile.delete();
	// }

	public void testValidateApprovalDevCap() throws Exception {
		File jdfFile = new File(TEST_DATA + "devcaps/Elk_Approval_DevCaps.jmf").getCanonicalFile();
		assertTrue("Path to JDF: " + jdfFile, jdfFile.exists());
		File schemaFile = new File(TEST_DATA + "schema/JDF.xsd").getCanonicalFile();
		assertTrue(schemaFile.exists());
		File devcapFile = null;
		File reportFile = File.createTempFile("report", ".xml");
		reportFile.delete();

		boolean result = CheckJDFWrapper.validate(jdfFile, schemaFile, devcapFile, reportFile);
		assertTrue("Validation failed: " + jdfFile, result);
		assertTrue(reportFile.exists());
		reportFile.delete();
	}

	public void testValidateApprovalJDF_ApprovalDevCap_NoSchema() throws Exception {
		// Approval JDF
		File jdfFile = new File(TEST_DATA + "jdf/Elk_Approval.jdf").getCanonicalFile();
		assertTrue("Path to JDF: " + jdfFile, jdfFile.exists());
		// Schema
		File schemaFile = null;
		// Approval DevCap
		File devcapFile = new File(TEST_DATA + "devcaps/Elk_Approval_DevCaps.jmf").getCanonicalFile();
		assertTrue(devcapFile.exists());
		// Report
		File reportFile = File.createTempFile("report", ".xml");
		reportFile.delete();
		// Validate
		boolean result = CheckJDFWrapper.validate(jdfFile, schemaFile, devcapFile, reportFile);
		assertTrue("Validation failed: " + jdfFile, result);
		assertTrue(reportFile.exists());
		reportFile.delete();
	}

	/**
	 * Tests that JMF files containing elements of non-JDF namespaces validate.
	 * 
	 * @throws Exception
	 */
	public void testValidateJMFWithNamespaces() throws Exception {
		File nsJMFDir = new File(TEST_DATA + "/jmf/namespaces");
		assertTrue(nsJMFDir.exists());
		assertTrue(nsJMFDir.isDirectory());
		File[] nsJMFFiles = nsJMFDir.listFiles(new JMFFileFilter());
		for (int i = 0; i < nsJMFFiles.length; i++) {
			File jmfFile = nsJMFFiles[i];
			System.out.println("Validating: " + jmfFile.getName());
			String jmf = IOUtils.toString(new FileInputStream(jmfFile));
			StringBuffer report = new StringBuffer();
			boolean result = CheckJDFWrapper.validate(jmf, report);
			System.out.println(report);// XXX
			assertTrue("Validation failed: " + jmfFile, result);
		}
	}

	/**
	 * Validates a JMF message with and explicitly stated JDF Schema. The schema used is the schema bundled with Alces.
	 */
	public void testValidateWithSchema() throws Exception {
		File schemaFile = new File(TEST_DATA + "schema/JDF.xsd");
		assertTrue("Schema file does not exist: " + schemaFile.toString(), schemaFile.exists());
		File devcapFile = null;
		File reportFile = null;
		boolean result = CheckJDFWrapper.validate(_jmfFiles[0], schemaFile, devcapFile, reportFile);
		assertTrue(result);
	}

	/**
	 * Validates a JMF message with and explicitly stated JDF Schema. The schema used is a temporary copy of the schema bundled with Alces. The only difference
	 * between the temporary copy and the origianl is that the copy's file name contains spaces.
	 * 
	 * @throws Exception
	 */
	public void testValidateWithSchema_SpacesInFilename() throws Exception {
		File schemaFile = new File(TEST_DATA + "schema/JDF.xsd");
		File devcapFile = null;
		File reportFile = File.createTempFile("report", ".xml");
		assertTrue("Schema file does not exist: " + schemaFile.toString(), schemaFile.exists());
		FileInputStream schemaIn = new FileInputStream(schemaFile);
		File newSchemaFile = new File(schemaFile.getParent(), "JDF Schema.xsd");
		newSchemaFile.deleteOnExit();
		assertFalse("New schema copy already exists: " + newSchemaFile, newSchemaFile.exists());
		FileOutputStream schemaOut = new FileOutputStream(newSchemaFile);
		IOUtils.copy(schemaIn, schemaOut);
		assertTrue("Schema has not been copied: " + newSchemaFile, newSchemaFile.exists());
		boolean result = CheckJDFWrapper.validate(_jmfFiles[0], newSchemaFile, devcapFile, reportFile);
		String report = IOUtils.toString(new FileInputStream(reportFile));
		assertTrue("JMF did not validate: " + report, result);
	}

	/**
	 * A filter that filters out directories first and then applies the filter provided in the constructor.
	 * 
	 * @author Claes Buckwalter (claes.buckwalter@agfa.com)
	 */
	private static final class TestFileFilter implements FileFilter {

		private final FileFilter filter;

		TestFileFilter(FileFilter baseFilter) {
			filter = baseFilter;
		}

		public boolean accept(File pathname) {
			final boolean accept;
			if (pathname.isDirectory()) {
				accept = false;
			} else {
				accept = filter.accept(pathname);
			}
			return accept;
		}
	}
}