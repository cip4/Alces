/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import java.util.Map;
import java.util.Properties;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.Message;
import org.cip4.tools.alces.message.OutMessageImpl;
import org.cip4.tools.alces.preprocessor.jmf.XPathPreprocessor;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class XPathPreprocessorTest extends AlcesTestCase {

    public void testPreprocess() throws Exception {
        XPathPreprocessor pp;
        Message jmf = new OutMessageImpl(null,
                getTestFileAsString("QueryKnownDevices.jmf"), true);
        pp = new XPathPreprocessor();
        Map xpathValuePairs = new Properties();
        xpathValuePairs.put("/jdf:JMF/jdf:Query/@ID", "ALCES12345");
        xpathValuePairs.put("/jdf:JMF/jdf:Query/jdf:DeviceFilter", "APAN");
        pp.setXpathValuePairs(xpathValuePairs);

        System.out.println("Before:\n" + jmf + "\n");
        String m0 = jmf.getBody();
        jmf = pp.preprocess(jmf);
        System.out.println("After:\n" + jmf + "\n");
        assertNotSame(m0, jmf.getBody());
    }

}
