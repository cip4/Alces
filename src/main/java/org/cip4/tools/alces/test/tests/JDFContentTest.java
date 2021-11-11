/*
 * Created on May 5, 2005
 */
package org.cip4.tools.alces.test.tests;

import org.cip4.tools.alces.model.AbstractJmfMessage;
import org.cip4.tools.alces.test.TestResult;
import org.cip4.tools.alces.test.TestResult.Result;
import org.cip4.tools.alces.test.TestResultImpl;
import org.cip4.tools.alces.util.JDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests that a <code>Message</code>'s body contains a JDF instance or a JMF
 * message, and that the message's content-type has the correct value.
 * 
 * @author Claes Buckwalter (clabu@itn.liu.se)
 * @version $Id$
 */
public class JDFContentTest extends Test {

    private final static Logger log = LoggerFactory.getLogger(JDFContentTest.class);

    public JDFContentTest() {
        super(
                "JDFContentTest - Tests that a message's body contains a JDF instance or a JMF "
                        + "message and that the message's header contains the corresponding "
                        + "HTTP header Content-type.");
    }

    @Override
	public TestResult runTest(AbstractJmfMessage message) {
        boolean passedTest = false;
        String logMsg = "Passed test.";
        String body = message.getBody();
        String contentType = message.getContentType(); 
        if (contentType == null || body == null) {
            passedTest = false;
            logMsg = "The message's content-type is not set or the body is empty.";
        } else if (contentType.startsWith(JDFConstants.JMF_CONTENT_TYPE)) {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            // Tests that JMF start tag occurs before JDF start tag
            passedTest = (jmfIndex != -1 && (jdfIndex == -1 || jmfIndex < jdfIndex));                        
            if (!passedTest) {
                logMsg = "The message's content-type is '" + 
                            JDFConstants.JMF_CONTENT_TYPE + 
                            "', but the body's root element is not JMF.";                
            }
        } else if (contentType.startsWith(JDFConstants.JDF_CONTENT_TYPE)) {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            // Tests that JDF start tag occurrs before JMF start tag
            passedTest = (jdfIndex != -1 && (jmfIndex == -1 || jdfIndex < jmfIndex));
            if (!passedTest) {
                logMsg = "The message's content-type is '" + 
                            JDFConstants.JDF_CONTENT_TYPE + 
                            "', but the body's root element is not JDF.";
            }
        } else if (contentType.startsWith(JDFConstants.MIME_CONTENT_TYPE)) {
        	int jmfIndex = body.indexOf(JDFConstants.JMF_CONTENT_TYPE);
        	int jdfIndex = body.indexOf(JDFConstants.JDF_CONTENT_TYPE);
        	passedTest = (jdfIndex != -1 && (jmfIndex == -1 || jdfIndex > jmfIndex));
        	if (!passedTest) {
        		logMsg = "The message's content-type is '" + JDFConstants.MIME_CONTENT_TYPE + 
        					"', but the MIME packages is missing a JDF part, or the JDF part is before the JMF part.";
        	}
        } else {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            if (jdfIndex != -1) {
                logMsg = "The message seems to contain JDF but the content-type is: " + 
                            message.getContentType() + "\nThe content-type should be: " + 
                            JDFConstants.JDF_CONTENT_TYPE;
                
                passedTest = false;
            } else if (jmfIndex != -1) {
                logMsg = "The message seems to contain JMF but the content-type is: " + 
                            message.getContentType() + "\nThe content-type should be: " + 
                            JDFConstants.JMF_CONTENT_TYPE;
                passedTest = false;
            } else {
                logMsg = "The message does not seem to contain JMF or JDF.\n" +
                        "The content-type was: " + message.getContentType();
                passedTest = false;
            }            
        }
        log.debug(logMsg);
        return new TestResultImpl(this, message, Result.getPassed(passedTest), logMsg);
    }
}
