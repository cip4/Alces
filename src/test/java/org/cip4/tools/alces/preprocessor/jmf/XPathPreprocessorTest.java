/*
 * Created on Apr 22, 2005
 */
package org.cip4.tools.alces.preprocessor.jmf;

import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.OutgoingJmfMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class XPathPreprocessorTest extends AlcesTestCase {

    @Test
    public void testPreprocess() throws Exception {
        XPathPreprocessor pp;
        AbstractJmfMessage jmf = new OutgoingJmfMessage(null,
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
        Assertions.assertNotSame(m0, jmf.getBody());
    }

}
