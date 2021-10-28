/*
 * Created on Jul 20, 2005
 */
package org.cip4.tools.alces.test.tests;

import org.apache.commons.io.IOUtils;
import org.cip4.tools.alces.util.JDFFileFilter;
import org.cip4.tools.alces.util.JMFFileFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Requires that 'user.dir' is set to 'Alces/src/bin' for CheckJDFWrapper's path to the JDF schema to be correct.
 * <p>
 * TODO Enable JDF tests.
 *
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class CheckJDFWrapperTest {

    private File[] _jdfFiles;

    private File[] _jmfFiles;

    private static final String TEST_DATA = initTestData();

    private static String initTestData() {

        String resPath = "/org/cip4/tools/alces/data/CheckJDFWrapperTest/";
        String s = CheckJDFWrapperTest.class.getResource(resPath).getFile();

        return s;
    }

    @BeforeEach
    public void setUp() throws Exception {
        /*
         * String userDir = System.getProperty("user.dir"); if (!userDir.endsWith("/src/bin")) { userDir = System.setProperty("user.dir", userDir + "/src/bin");
         * }
         */

        File jdfDir = new File(TEST_DATA + "jdf/").getCanonicalFile();
        Assertions.assertTrue(jdfDir.exists(), "JDF dir does not exist: " + jdfDir.getAbsolutePath());
        _jdfFiles = jdfDir.listFiles(new TestFileFilter(new JDFFileFilter()));
        Assertions.assertTrue(_jdfFiles.length > 0, "No JDF files in:" + jdfDir);
        File jmfDir = new File(TEST_DATA + "jmf/").getCanonicalFile();
        Assertions.assertTrue(jmfDir.exists(), "JMF dir does not exist: " + jmfDir.getAbsolutePath());
        _jmfFiles = jmfDir.listFiles(new TestFileFilter(new JMFFileFilter()));
        Assertions.assertTrue(_jmfFiles.length > 0, "No JMF files in:" + jmfDir);
    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateInputStreamFile_JMF() throws Exception {
        File reportFile = File.createTempFile("validation_report", ".xml"); // Create
        // a
        // temp
        // file
        reportFile.delete();
        for (int i = 0; i < _jmfFiles.length; i++) {
            boolean result = CheckJDFWrapper.validate(new FileInputStream(_jmfFiles[i]), reportFile);
            Assertions.assertTrue(result, "Validation failed: " + i + ". " + _jmfFiles[i]);
            Assertions.assertTrue(reportFile.exists(), "Report missing for JMF file " + i + ": " + _jmfFiles[i]);
            reportFile.delete();
        }
    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateInputStreamFile_JMFNull() throws Exception {
        StringBuffer resultLog = new StringBuffer();
        for (int i = 0; i < _jmfFiles.length; i++) {
            boolean result = CheckJDFWrapper.validate(new FileInputStream(_jmfFiles[i]), resultLog);
            Assertions.assertTrue(result, "Validation failed: " + i + ". " + _jmfFiles[i]);
        }
    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateString_JMF() throws Exception {
        for (int i = 0; i < _jmfFiles.length; i++) {
            String data = IOUtils.toString(new FileInputStream(_jmfFiles[i]));
            boolean result = CheckJDFWrapper.validate(data);
            Assertions.assertTrue(result, "Validation failed: " + i + ". " + _jmfFiles[i]);
        }
    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateStringStringBuffer_JMF() throws Exception {
        String data = IOUtils.toString(new FileInputStream(_jmfFiles[0]));
        StringBuffer report = new StringBuffer();
        boolean result = CheckJDFWrapper.validate(data, report);
        Assertions.assertNotNull(report, "Report is null");
        Assertions.assertFalse(report.length() == 0, "Report is empty");
        Assertions.assertTrue(result, "Validation failed: " + _jmfFiles[0]);

    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateStringFile_JMF() throws Exception {
        File reportFile = File.createTempFile("validation_report", ".xml");
        reportFile.delete();
        for (int i = 0; i < _jmfFiles.length; i++) {
            String data = IOUtils.toString(new FileInputStream(_jmfFiles[i]));
            boolean result = CheckJDFWrapper.validate(data, reportFile);
            Assertions.assertTrue(result, "Validation failed: " + i + ". " + _jmfFiles[i]);
            Assertions.assertTrue(reportFile.exists());
            reportFile.delete();
        }
    }

    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateFileFile_JMF() throws Exception {
        File reportFile = File.createTempFile("validation_report", ".xml");
        reportFile.delete();
        for (int i = 0; i < _jmfFiles.length; i++) {
            boolean result = CheckJDFWrapper.validate(_jmfFiles[i], reportFile);
            Assertions.assertTrue(result, "Validation failed: " + i + ". " + _jmfFiles[i]);
            Assertions.assertTrue(reportFile.exists());
            reportFile.delete();
        }
    }

    /**
     * Disabled until a DevCaps file for JMF has been written.
     */
    public void disabled_testValidateFileFileFileFile_JMF() throws Exception {
        File schemaFile = new File(TEST_DATA + "schema/JDF.xsd").getCanonicalFile();
        Assertions.assertTrue(schemaFile.exists());
        File devcapFile = new File(TEST_DATA + "devcaps/DevCaps_LayCrImp.jdf").getCanonicalFile();
        Assertions.assertTrue(devcapFile.exists());
        File reportFile = File.createTempFile("report", ".xml");
        reportFile.delete();
        for (int i = 0; i < _jmfFiles.length; i++) {
            boolean result = CheckJDFWrapper.validate(_jmfFiles[i], schemaFile, devcapFile, reportFile);
            Assertions.assertTrue(result, "Validation failed: \" + _jmfFiles[i]");
            Assertions.assertTrue(reportFile.exists());
            reportFile.delete();
        }
    }

    @org.junit.jupiter.api.Test
    public void testValidateFileFileNullFileNullFile_JMF() throws Exception {
        File schemaFile = null;
        File devcapFile = null;
        File reportFile = null;
        for (int i = 0; i < _jmfFiles.length; i++) {
            boolean result = CheckJDFWrapper.validate(_jmfFiles[i], schemaFile, devcapFile, reportFile);
            Assertions.assertTrue(result);
        }
    }

    @org.junit.jupiter.api.Test
    public void testValidateApprovalDevCap() throws Exception {
        File jdfFile = new File(TEST_DATA + "devcaps/Elk_Approval_DevCaps.jmf").getCanonicalFile();
        Assertions.assertTrue(jdfFile.exists(), "Path to JDF: " + jdfFile);
        File schemaFile = new File(TEST_DATA + "schema/JDF.xsd").getCanonicalFile();
        Assertions.assertTrue(schemaFile.exists());
        File devcapFile = null;
        File reportFile = File.createTempFile("report", ".xml");
        reportFile.delete();

        boolean result = CheckJDFWrapper.validate(jdfFile, schemaFile, devcapFile, reportFile);
        Assertions.assertTrue(result, "Validation failed: " + jdfFile);
        Assertions.assertTrue(reportFile.exists());
        reportFile.delete();
    }

    @org.junit.jupiter.api.Test
    public void testValidateApprovalJDF_ApprovalDevCap_NoSchema() throws Exception {
        // Approval JDF
        File jdfFile = new File(TEST_DATA + "jdf/Elk_Approval.jdf").getCanonicalFile();
        Assertions.assertTrue(jdfFile.exists(), "Path to JDF: " + jdfFile);
        // Schema
        File schemaFile = null;
        // Approval DevCap
        File devcapFile = new File(TEST_DATA + "devcaps/Elk_Approval_DevCaps.jmf").getCanonicalFile();
        Assertions.assertTrue(devcapFile.exists());
        // Report
        File reportFile = File.createTempFile("report", ".xml");
        reportFile.delete();
        // Validate
        boolean result = CheckJDFWrapper.validate(jdfFile, schemaFile, devcapFile, reportFile);
        Assertions.assertTrue(result, "Validation failed: " + jdfFile);
        Assertions.assertTrue(reportFile.exists());
        reportFile.delete();
    }

    /**
     * Tests that JMF files containing elements of non-JDF namespaces validate.
     *
     * @throws Exception
     */
    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testValidateJMFWithNamespaces() throws Exception {
        File nsJMFDir = new File(TEST_DATA + "/jmf/namespaces");
        Assertions.assertTrue(nsJMFDir.exists());
        Assertions.assertTrue(nsJMFDir.isDirectory());
        File[] nsJMFFiles = nsJMFDir.listFiles(new JMFFileFilter());
        for (int i = 0; i < nsJMFFiles.length; i++) {
            File jmfFile = nsJMFFiles[i];
            System.out.println("Validating: " + jmfFile.getName());
            String jmf = IOUtils.toString(new FileInputStream(jmfFile));
            StringBuffer report = new StringBuffer();
            boolean result = CheckJDFWrapper.validate(jmf, report);
            System.out.println(report);// XXX
            Assertions.assertTrue(result, "Validation failed: " + jmfFile);
        }
    }

    /**
     * Validates a JMF message with and explicitly stated JDF Schema. The schema used is the schema bundled with Alces.
     */
    @org.junit.jupiter.api.Test
    public void testValidateWithSchema() throws Exception {
        File schemaFile = new File(TEST_DATA + "schema/JDF.xsd");
        Assertions.assertTrue(schemaFile.exists(), "Schema file does not exist: " + schemaFile.toString());
        File devcapFile = null;
        File reportFile = null;
        boolean result = CheckJDFWrapper.validate(_jmfFiles[0], schemaFile, devcapFile, reportFile);
        Assertions.assertTrue(result);
    }

    /**
     * Validates a JMF message with and explicitly stated JDF Schema. The schema used is a temporary copy of the schema bundled with Alces. The only difference
     * between the temporary copy and the origianl is that the copy's file name contains spaces.
     *
     * @throws Exception
     */
    @Test
    public void testValidateWithSchema_SpacesInFilename() throws Exception {
        File schemaFile = new File(TEST_DATA + "schema/JDF.xsd");
        File devcapFile = null;
        File reportFile = File.createTempFile("report", ".xml");
        Assertions.assertTrue(schemaFile.exists(), "Schema file does not exist: " + schemaFile.toString());
        FileInputStream schemaIn = new FileInputStream(schemaFile);
        File newSchemaFile = new File(schemaFile.getParent(), "JDF Schema.xsd");
        newSchemaFile.deleteOnExit();
        Assertions.assertFalse(newSchemaFile.exists(), "New schema copy already exists: " + newSchemaFile);
        FileOutputStream schemaOut = new FileOutputStream(newSchemaFile);
        IOUtils.copy(schemaIn, schemaOut);
        Assertions.assertTrue(newSchemaFile.exists(), "Schema has not been copied: " + newSchemaFile);
        boolean result = CheckJDFWrapper.validate(_jmfFiles[0], newSchemaFile, devcapFile, reportFile);
        String report = IOUtils.toString(new FileInputStream(reportFile));
        Assertions.assertTrue(result, "JMF did not validate: " + report);
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
