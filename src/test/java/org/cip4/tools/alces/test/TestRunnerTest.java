package org.cip4.tools.alces.test;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRunnerTest extends AlcesTestCase {

    @Test
    public void testUrlUtil() {
        final File file1 = getTestFileAsFile("content/file1.pdf");

        Assertions.assertNotNull(file1);
        Assertions.assertTrue(file1.exists());
    }
}
