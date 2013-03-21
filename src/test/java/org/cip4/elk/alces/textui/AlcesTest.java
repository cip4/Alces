package org.cip4.elk.alces.textui;

import java.io.File;

import org.cip4.elk.alces.junit.AlcesTestCase;

public class AlcesTest extends AlcesTestCase {

	public void testMain_singleFile() throws Exception {				
		File scriptFile = new File("src/bin/alces.js");
		assertTrue(scriptFile.canRead());
		File testFile = new File("src/testdata/01.KnownDevices.jmf");
		assertTrue(testFile.canRead());
		File propsFile = new File("src/test/data/AlcesTest/alces.properties");
		assertTrue(propsFile.canRead());
		Alces.runScript(scriptFile, "http://example.org", testFile, propsFile);
	}

}
