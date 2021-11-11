/*
 * Created on Sep 28, 2004
 */
package org.cip4.tools.alces.message.util.mime;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.MimeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class MimeReaderTest extends AlcesTestCase {

    @BeforeEach
    public void setUp() throws Exception {
    }

    /**
     * Extracts a JMF and a JDF from a MIME package.
     */
    @Test
    public void testParseMime_JMF_JDF() throws IOException, URISyntaxException {
        // Setup path to MIME package
        String mimeResource = "SubmitQueueEntry.jmf_Approval.jdf.mjm";
        // Setup output dir
        File outputDir = File.createTempFile("MimeReaderTest", "-output");
        outputDir.deleteOnExit();
        Assertions.assertTrue(outputDir.delete());
        Assertions.assertTrue(outputDir.mkdir());
        outputDir.deleteOnExit();
        Assertions.assertTrue(outputDir.exists());
        String outputDirUrl = outputDir.toURL().toExternalForm();
        Assertions.assertTrue(new File(new URI(outputDirUrl)).exists(), "Output dir URL does not exists: " + outputDirUrl);
        // Extract files form MIME package
        InputStream mimeStream = getTestFileAsStream(mimeResource);
        Assertions.assertNotNull(mimeStream, "Input stream was null for file: " + mimeResource);
        String[] fileUrls = MimeUtil.extractMimePackage(mimeStream,
                outputDir.toURI().toURL().toExternalForm());
        Assertions.assertTrue(fileUrls.length == 2);
        for (int i = 0; i < fileUrls.length; i++) {
            File mimePart = new File(new URI(fileUrls[i]));
            Assertions.assertTrue( mimePart.exists(), "Extracted MIME part does not exist: " + mimePart.getCanonicalPath());
            mimePart.deleteOnExit();
        }
    }
}
