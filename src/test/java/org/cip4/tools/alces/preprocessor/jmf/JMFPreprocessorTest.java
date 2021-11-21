/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JMFPreprocessorTest extends AlcesTestCase {

    /*
     * Class under test for void JMFTransformer()
     */
    @Test
    public void testJMFTransformer() throws Exception {
        JMFPreprocessor t = new JMFPreprocessor();
        Assertions.assertEquals(t.getSenderId(), JMFPreprocessor.DEFAULT_SENDER_ID);
    }

    /*
     * Class under test for void JMFTransformer(String)
     */
    @Test
    public void testJMFTransformerString() throws Exception {
        JMFPreprocessor t = new JMFPreprocessor("Alces");
        Assertions.assertEquals(t.getSenderId(), "Alces");
    }

    /*
     * Class under test for Object doTransform(Object)
     */
    @Test
    public void testPreprocess() throws Exception {
        JMFPreprocessor pp;
        AbstractJmfMessage jmf = new OutgoingJmfMessage(null, getTestFileAsString("QueryKnownDevices.jmf"), true);

        pp = new JMFPreprocessor();
        pp.setSenderId("Alces");

        String m0 = jmf.getBody();
        System.out.println("Before:\n" + jmf + "\n");

        jmf = pp.preprocess(jmf);
        Assertions.assertNotSame(m0, jmf.getBody());
        System.out.println("After:\n" + jmf + "\n");
    }

}
