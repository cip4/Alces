/*
 * Created on May 21, 2006
 */
package org.cip4.tools.alces.test.tests;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.util.JDFConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.io.IOException;

public class ReturnQueueEntryTestTest extends AlcesTestCase {

    @org.junit.jupiter.api.Test
    public void testFileSystem() throws IOException {
        FileSystemManager fsm = VFS.getManager();
        FileObject fo = fsm.resolveFile(System.getProperty("user.dir"));
        Assertions.assertTrue(fo.exists());
    }

    /*
     * Test method for
     * 'org.cip4.tools.alces.test.tests.SubmitQueueEntryTest.runTest(Message)'
     */
    @org.junit.jupiter.api.Test
    @Disabled("Disabled failing test for future analysis.")
    public void testRunTestFileURL() throws Exception {
        // Read JMF
        JDFJMF jmf = getTestFileAsJMF("CommandReturnQueueEntry-Approval.jmf");
        // Resolve and update JDF reference in JMF
        String jdfUrl = jmf.getCommand(0).getReturnQueueEntryParams(0).getURL();
        File jdfFile = new File(jdfUrl);
        Assertions.assertTrue(jdfFile.exists(), "JDF file referenced by JMF does not exist: " + jdfFile.getAbsolutePath());
        jmf.getCommand(0).getReturnQueueEntryParams(0).setURL(jdfFile.toURI().toASCIIString());
        // Create Message and run test
        InMessage msg = new InMessageImpl(JDFConstants.JMF_CONTENT_TYPE, jmf.toXML(), true);
        Test test = new ReturnQueueEntryTest();
        TestResult result = test.runTest(msg);
        System.out.println("Validated: " + result.isPassed());
        System.out.println(result.getResultString());
        Assertions.assertTrue(result.isPassed());
    }
}
