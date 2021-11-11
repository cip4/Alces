/*
 * Created on May 9, 2005
 */
package org.cip4.tools.alces.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.tests.CheckJDFTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JMFElementFactoryTest {

    @Test
    @Disabled("Disabled failing test for future analysis.")
    public void testCreateJMF() throws Exception {
        JMFMessageFactory factory = JMFMessageFactory.getInstance();
        JDFJMF jmf = factory.createJMF();
        Assertions.assertNotNull(jmf);

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);
        System.out.println(jmfString);

        IncomingJmfMessage msg = new IncomingJmfMessage(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.isPassed(), "Message was not valid: " + jmfString);

        System.out.println(result.getResultString());
    }

    @Test
    @Disabled("Disabled failing test for future analysis.")
    public void testCreateQueryKnownMessages() throws Exception {
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryKnownMessages");

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);
        System.out.println(jmfString);

        IncomingJmfMessage msg = new IncomingJmfMessage(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.isPassed(), "Message not valid: " + jmfString);

        System.out.println(result.getResultString());
    }

    @Test
    public void testCreateQueryStatus() throws Exception {
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryStatus");
        System.out.println(jmf.toXML());

    }


    @Test
    public void testCreateIncorrectXsiType() throws Exception {
        boolean failed = false;
        try {
            JMFMessageFactory.getInstance().createJMF("asdf");
        } catch (Exception e) {
            failed = true;
        }
        Assertions.assertTrue(failed);
    }
}
