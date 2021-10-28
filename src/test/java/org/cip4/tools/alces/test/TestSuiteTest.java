/*
 * Created on Apr 24, 2005
 */
package org.cip4.tools.alces.test;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.message.OutMessage;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.transport.HttpDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TestSuiteTest extends AlcesTestCase {

    @Test
    public void testFindTestSession() throws Exception {
        // Create a pool for all test sessions
        TestSuiteImpl pool = new TestSuiteImpl();
        // Create a test session
        TestSessionImpl session = new TestSessionImpl("http://127.0.0.1:8080/tools/jmf");
        pool.addTestSession(session);
        // Load and send a message
        OutMessage mOut = new OutMessageImpl("",
                getTestFileAsString("QueryKnownMessages.jmf"), true);
        HttpDispatcher disp = new HttpDispatcher();
        session.sendMessage(mOut, disp);
        // Load and fake message that matches the sent messsage
        InMessage mIn = new InMessageImpl("",
                getTestFileAsString("ResponseKnownMessages.jmf"), false);
        // Find the test session the fake message belongs to
        TestSession foundSession = pool.findTestSession(mIn);
        Assertions.assertSame(session, foundSession);
        // Load and fake message that matches the sent messsage
        InMessage mIn2 = new InMessageImpl("",
                getTestFileAsString("ResponseKnownMessages2.jmf"), false);
        // Find the test session the fake message belongs to
        TestSession foundSession2 = pool.findTestSession(mIn2);
        Assertions.assertSame(session, foundSession2);

    }
}
