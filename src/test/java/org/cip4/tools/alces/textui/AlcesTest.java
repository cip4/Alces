package org.cip4.tools.alces.textui;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.AlcesPathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class AlcesTest extends AlcesTestCase {

    @Test
    public void testMain_singleFile() throws Exception {
        File scriptFile = new File(AlcesPathUtil.ALCES_BIN_DIR + File.separator + "alces.js");
        Assert.assertTrue(scriptFile.canRead());

        File testFile = getTestFileAsFile("01.KnownDevices.jmf");
        Assert.assertTrue(testFile.canRead());

        File propsFile = getTestFileAsFile("alces.properties");
        Assert.assertTrue(propsFile.canRead());
        Alces.runScript(scriptFile, "http://example.org", testFile, propsFile);
    }

}
