/*
 * Created on May 15, 2005
 */
package org.cip4.tools.alces.test;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.transport.HttpDispatcher;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Requires that Elk is running on http://elk.itn.liu.se/tools/jmf
 *
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestSessionXMLBindingTest extends AlcesTestCase {

    @Test
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
}
