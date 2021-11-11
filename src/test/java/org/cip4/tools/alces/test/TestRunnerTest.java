package org.cip4.tools.alces.test;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.resource.process.JDFFileSpec;
import org.cip4.jdflib.util.MimeUtil;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.model.OutgoingJmfMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class TestRunnerTest extends AlcesTestCase {

    @Test
    public void testUrlUtil() {
        final File file1 = getTestFileAsFile("content/file1.pdf");

        Assertions.assertNotNull(file1);
        Assertions.assertTrue(file1.exists());
    }
}
