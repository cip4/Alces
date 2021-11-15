/*
 * Created on May 9, 2005
 */
package org.cip4.tools.alces.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.tests.CheckJDFTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TemplateJMFElementFactoryTest {

    @Test
    @Disabled("Disabled failing test for future analysis.")
    public void testCreateJMF() throws Exception {
        System.setProperty("org.cip4.tools.alces.jmf.JMFMessageFactory", "org.cip4.tools.alces.jmf.TemplateJMFMessageFactory");

        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("Connect_KnownDevices");

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);
        System.out.println(jmfString);

        IncomingJmfMessage msg = new IncomingJmfMessage(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assertions.assertTrue(result.isPassed());

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
        Assertions.assertTrue(result.isPassed());

        System.out.println(result.getResultString());
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
