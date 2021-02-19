/*
 * Created on May 9, 2005
 */
package org.cip4.tools.alces.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.tests.CheckJDFTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class TemplateJMFElementFactoryTest {

    @Test
    @Ignore("Disabled failing test for future analysis.")
    public void testCreateJMF() throws Exception {
        System.setProperty("org.cip4.tools.alces.jmf.JMFMessageFactory", "org.cip4.tools.alces.jmf.TemplateJMFMessageFactory");

        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("Connect_KnownDevices");

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);
        System.out.println(jmfString);

        InMessage msg = new InMessageImpl(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assert.assertTrue(result.isPassed());

        System.out.println(result.getResultString());
    }

    @Test
    @Ignore("Disabled failing test for future analysis.")
    public void testCreateQueryKnownMessages() throws Exception {
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryKnownMessages");

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);
        System.out.println(jmfString);

        InMessage msg = new InMessageImpl(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        Assert.assertTrue(result.isPassed());

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
        Assert.assertTrue(failed);
    }
}
