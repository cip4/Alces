/*
 * Created on May 5, 2005
 */
package org.cip4.tools.alces.service.testrunner.jmftest;

import org.cip4.jdflib.core.JDFConstants;
import org.cip4.tools.alces.service.testrunner.model.AbstractJmfMessage;
import org.cip4.tools.alces.service.testrunner.model.TestResult;
import org.cip4.tools.alces.service.testrunner.model.TestResult.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Tests that a jmf message's body contains a JDF instance or a JMF
 * message, and that the message's content-type has the correct value.
 */
@Component
public class JDFContentJmfTest implements JmfTest {

    private final static Logger log = LoggerFactory.getLogger(JDFContentJmfTest.class);

    @Override
    public Type getType() {
        return Type.JMF_BOTH_TEST;
    }

    @Override
    public String getDescription() {
        return "JDFContentTest - Tests that a message's body contains a JDF instance or a JMF "
                + "message and that the message's header contains the corresponding "
                + "HTTP header Content-type.";
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
        } else if (contentType.startsWith(JDFConstants.MIME_JMF)) {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            // Tests that JMF start tag occurs before JDF start tag
            passedTest = (jmfIndex != -1 && (jdfIndex == -1 || jmfIndex < jdfIndex));                        
            if (!passedTest) {
                logMsg = "The message's content-type is '" +
                        JDFConstants.MIME_JMF +
                            "', but the body's root element is not JMF.";                
            }
        } else if (contentType.startsWith(JDFConstants.MIME_JDF)) {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            // Tests that JDF start tag occurrs before JMF start tag
            passedTest = (jdfIndex != -1 && (jmfIndex == -1 || jdfIndex < jmfIndex));
            if (!passedTest) {
                logMsg = "The message's content-type is '" + 
                            JDFConstants.MIME_JDF +
                            "', but the body's root element is not JDF.";
            }
        } else if (contentType.startsWith(MediaType.MULTIPART_RELATED_VALUE)) {
        	int jmfIndex = body.indexOf(JDFConstants.MIME_JMF);
        	int jdfIndex = body.indexOf(JDFConstants.MIME_JDF);
        	passedTest = (jdfIndex != -1 && (jmfIndex == -1 || jdfIndex > jmfIndex));
        	if (!passedTest) {
        		logMsg = "The message's content-type is '" + MediaType.MULTIPART_RELATED_VALUE +
        					"', but the MIME packages is missing a JDF part, or the JDF part is before the JMF part.";
        	}
        } else {
            int jdfIndex = body.indexOf("<JDF");
            int jmfIndex = body.indexOf("<JMF");
            if (jdfIndex != -1) {
                logMsg = "The message seems to contain JDF but the content-type is: " + 
                            message.getContentType() + "\nThe content-type should be: " + 
                            JDFConstants.MIME_JDF;
                
                passedTest = false;
            } else if (jmfIndex != -1) {
                logMsg = "The message seems to contain JMF but the content-type is: " + 
                            message.getContentType() + "\nThe content-type should be: " + 
                            JDFConstants.MIME_JMF;
                passedTest = false;
            } else {
                logMsg = "The message does not seem to contain JMF or JDF.\n" +
                        "The content-type was: " + message.getContentType();
                passedTest = false;
            }            
        }
        log.debug(logMsg);
        return new TestResult(this, message, passedTest ? Result.PASSED : Result.FAILED, logMsg);
    }
}
