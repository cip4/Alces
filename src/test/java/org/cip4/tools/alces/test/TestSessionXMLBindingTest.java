/*
 * Created on May 15, 2005
 */
package org.cip4.tools.alces.test;

import java.io.File;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.transport.HttpDispatcher;

/**
 * Requires that Elk is running on http://elk.itn.liu.se/tools/jmf
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestSessionXMLBindingTest extends AlcesTestCase {

	public void testSerialize() throws Exception {
		// Create a pool for all test sessions
		TestSuiteImpl suite = new TestSuiteImpl();
		// Create a test session
		HttpDispatcher disp = new HttpDispatcher();
		TestSessionImpl session = new TestSessionImpl("http://www.cip4.org");
		suite.addTestSession(session);
		// Load and send a message
		OutMessage mOut = new OutMessageImpl("", getTestFileAsString("QueryKnownMessages.jmf"), true);
		session.sendMessage(mOut, disp);
		// Load and fake message that matches the sent messsage
		InMessage mIn = new InMessageImpl("", getTestFileAsString("ResponseKnownMessages.jmf"), true);
		session.receiveMessage(mIn);
		TestSuiteSerializer ser = new TestSuiteSerializer();
		File outputDir = new File(System.getProperty("java.io.tmpdir"), "TestSessionXMLBindingTest_" + System.currentTimeMillis());
		ser.serialize(suite, outputDir.getAbsolutePath());
	}

	/*
	 * public void testToXML() throws Exception { // Create a pool for all test sessions TestSuiteImpl pool = new TestSuiteImpl(); // Create a test session
	 * TestSessionImpl session = new TestSessionImpl("http://127.0.0.1:8080/tools/jmf"); pool.addTestSession(session); // Load and send a message OutMessage
	 * mOut = new OutMessageImpl("", getResourceAsString("KnownDevices.xml"), true); session.sendMessage(mOut); // Load and fake message that matches the sent
	 * messsage InMessage mIn = new InMessageImpl("", getResourceAsString("KnownDevicesReply.xml"), true); // Find the test session the fake message belongs to
	 * TestSession foundSession = pool.findTestSession(mIn); //assertSame(session, foundSession); XStream xstream = new XStream(new DomDriver()); // does not
	 * require XPP3 library String xml = xstream.toXML(mOut); System.out.println(xml); }
	 */

}
