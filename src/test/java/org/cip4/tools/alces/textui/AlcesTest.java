package org.cip4.tools.alces.textui;

import java.io.File;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.util.AlcesPathUtil;

public class AlcesTest extends AlcesTestCase {

	public void testMain_singleFile() throws Exception {
		File scriptFile = new File(AlcesPathUtil.ALCES_BIN_DIR + File.separator + "alces.js");
		assertTrue(scriptFile.canRead());

		File testFile = getTestFileAsFile("01.KnownDevices.jmf");
		assertTrue(testFile.canRead());

		File propsFile = getTestFileAsFile("alces.properties");
		assertTrue(propsFile.canRead());
		Alces.runScript(scriptFile, "http://example.org", testFile, propsFile);
	}

}
