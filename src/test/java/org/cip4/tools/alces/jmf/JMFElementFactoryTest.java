/*
 * Created on May 9, 2005
 */
package org.cip4.tools.alces.jmf;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.tools.alces.jmf.JMFMessageFactory;
import org.cip4.tools.alces.junit.AlcesTestCase;
import org.cip4.tools.alces.message.InMessage;
import org.cip4.tools.alces.message.InMessageImpl;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.tests.CheckJDFTest;

/**
 * @author Claes Buckwalter (clabu@itn.liu.se)
 */
public class JMFElementFactoryTest extends AlcesTestCase {

    public void testCreateJMF() throws Exception {
        JMFMessageFactory factory = JMFMessageFactory.getInstance();        
        JDFJMF jmf = factory.createJMF();
        assertNotNull(jmf);

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);                
        System.out.println(jmfString);
        
        InMessage msg = new InMessageImpl(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);        
        assertTrue("Message was not valid: " + jmfString, result.isPassed());
                    
        System.out.println(result.getResultString());
    }
    
    public void testCreateQueryKnownMessages() throws Exception {
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryKnownMessages");

        String jmfString = jmf.getOwnerDocument_KElement().write2String(2);                
        System.out.println(jmfString);
        
        InMessage msg = new InMessageImpl(null, jmfString, true);
        CheckJDFTest test = new CheckJDFTest();
        TestResult result = test.runTest(msg);
        assertTrue("Message not valid: " + jmfString, result.isPassed());
                    
        System.out.println(result.getResultString());  
    }
    
    public void testCreateQueryStatus() throws Exception {
        JDFJMF jmf = JMFMessageFactory.getInstance().createJMF("QueryStatus");
        System.out.println(jmf.toXML());
        
    }
    
    
    public void testCreateIncorrectXsiType() throws Exception {
        boolean failed = false;
        try {
            JMFMessageFactory.getInstance().createJMF("asdf");    
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed);        
    }
}
