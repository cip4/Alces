package org.cip4.tools.alces.textui;

import java.io.File;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.textui.Alces;

public class AlcesTest extends AlcesTestCase {

	public void testMain_singleFile() throws Exception {
		
//		InputStream is = AlcesTest.class.getResourceAsStream("/org/cip4/tools/alces/conf/log4j.xml");
//		LogManager.getLogManager().readConfiguration(is);
		
		File scriptFile = new File("src/main/assemble/bin/alces.js");
		assertTrue(scriptFile.canRead());
		
		String resTestFile = "/org/cip4/tools/alces/testdata/01.KnownDevices.jmf";
		File testFile = new File(AlcesTest.class.getResource(resTestFile).getFile());
		assertTrue(testFile.canRead());
		
		String resPropsFile = "/org/cip4/tools/alces/data/AlcesTest/alces.properties";
		File propsFile = new File(AlcesTest.class.getResource(resPropsFile).getFile());
		assertTrue(propsFile.canRead());
		Alces.runScript(scriptFile, "http://example.org", testFile, propsFile);
	}

}
