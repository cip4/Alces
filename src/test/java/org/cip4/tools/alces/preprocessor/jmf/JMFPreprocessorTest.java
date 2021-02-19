/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(t.getSenderId(), JMFPreprocessor.DEFAULT_SENDER_ID);
    }

    /*
     * Class under test for void JMFTransformer(String)
     */
    @Test
    public void testJMFTransformerString() throws Exception {
        JMFPreprocessor t = new JMFPreprocessor("Alces");
        Assert.assertEquals(t.getSenderId(), "Alces");
    }

    /*
     * Class under test for Object doTransform(Object)
     */
    @Test
    public void testPreprocess() throws Exception {
        JMFPreprocessor pp;
        Message jmf = new OutMessageImpl(null, getTestFileAsString("QueryKnownDevices.jmf"), true);

        pp = new JMFPreprocessor();
        pp.setSenderId("Alces");

        String m0 = jmf.getBody();
        System.out.println("Before:\n" + jmf + "\n");

        jmf = pp.preprocess(jmf);
        Assert.assertNotSame(m0, jmf.getBody());
        System.out.println("After:\n" + jmf + "\n");
    }

}
