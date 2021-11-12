package org.cip4.tools.alces.controller;

import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.jmf.JMFMessageBuilder;
import org.cip4.tools.alces.model.IncomingJmfMessage;
import org.cip4.tools.alces.test.TestRunner;
import org.cip4.tools.alces.test.TestSession;
import org.cip4.tools.alces.util.AlcesPathUtil;
import org.cip4.tools.alces.util.ConfigurationHandler;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

@RestController
public class JmfController {

    private static final Logger log = LoggerFactory.getLogger(JmfController.class);

    private static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";
    private static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";
    private static final String MIME_CONTENT_TYPE = "multipart/related";

    private ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();

    private final String testDataDir = AlcesPathUtil.ALCES_TEST_DATA_DIR;

    @RequestMapping(value = "/jdf/{filename}", method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
    public byte[] loadJdfAsset(@PathVariable String filename) throws IOException {

        Path jdfDir = Paths.get(testDataDir, "testdata", "jdf", filename);

        log.info("New path: {}", jdfDir);
        return Files.readAllBytes(jdfDir);
    }

    @RequestMapping(value = "/alces/jmf", method = RequestMethod.POST, consumes = {JMF_CONTENT_TYPE, MIME_CONTENT_TYPE}, produces = JMF_CONTENT_TYPE)
    public ResponseEntity<String> processJmf(
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader("Content-Type") String contentType,
            @RequestBody String messageBody,
            HttpServletRequest request
    ) {

        log.info("Receiving message from {} @ {} ({})...", userAgent, request.getRemoteHost(), request.getRemoteAddr());

        String remoteAddr = request.getRemoteAddr();
        String header = convertHttpHeadersToString(request);
        final IncomingJmfMessage inMessage = TestRunner.getInstance().getTestSuite().createInMessage(contentType, header, messageBody, false);

        // create and send response
        final JDFJMF jmfIn = JmfUtil.getBodyAsJMF(inMessage);
        ResponseEntity<String> responseEntity;

        if (jmfIn != null) {
            if (jmfIn.getAcknowledge(0) != null) {
                log.debug("Receiving Acknowledge message...");
                startTestSession(inMessage, remoteAddr);
                responseEntity = ResponseEntity.ok().build();

            } else if (jmfIn.getSignal(0) != null) {
                log.debug("Receiving Signal message...");
                startTestSession(inMessage, remoteAddr);
                responseEntity = ResponseEntity.ok().build();

            } else if (jmfIn.getMessageElement(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.ReturnQueueEntry, 0) != null) {
                log.debug("Receiving RetunQueueEntry message...");
                startTestSession(inMessage, remoteAddr);
                JDFJMF jmfOut = JMFMessageBuilder.buildResponse(jmfIn);
                responseEntity = ResponseEntity.ok(jmfOut.toXML());

            } else {
                log.debug("Receiving unhandled JMF message...");
                startTestSession(inMessage, remoteAddr);
                JDFJMF jmfOut = JMFMessageBuilder.buildNotImplementedResponse(jmfIn);
                jmfOut.getResponse(0).setReturnCode(Integer.parseInt(configurationHandler.getProp(ConfigurationHandler.JMF_NOT_IMPLEMENTED_RETURN_CODE)));
                responseEntity = ResponseEntity.ok(jmfOut.toXML());
            }


        } else if (contentType.equals(MIME_CONTENT_TYPE)) {
            log.debug("Receiving MIME package...");
            startTestSession(inMessage, remoteAddr);
            responseEntity = ResponseEntity.ok().build();

        } else if (contentType.startsWith(JDF_CONTENT_TYPE)) {
            log.debug("Receiving JDF file...");
            startTestSession(inMessage, remoteAddr);
            responseEntity = ResponseEntity.ok().build();

        } else {
            log.debug("Unknown content-type '" + contentType + "'...");
            startTestSession(inMessage, remoteAddr);
            responseEntity = ResponseEntity.badRequest().build();
        }

        return responseEntity;
    }

    /**
     * Converts the headers in a <code>HttpServletRequest</code> to a
     * <code>String</code>.
     *
     * @param request
     * @return
     */
    private static String convertHttpHeadersToString(HttpServletRequest request) {
        StringBuffer header = new StringBuffer();
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
            String headerName = (String) e.nextElement();
            header.append(headerName);
            header.append(": ");
            header.append(request.getHeader(headerName));
            header.append("\n");
        }
        return header.toString();
    }

    /**
     * Helper method to start a test session
     */
    private void startTestSession(IncomingJmfMessage inMessage, String remoteAddr) {

            // get test sesstion for in-message
            TestSession testSession = TestRunner.getInstance().getTestSuite().findTestSession(inMessage);

            // Add the message to the TestSession
            if (testSession != null) {
                testSession.receiveMessage(inMessage);

            } else {
                log.warn("No test session found that matches the message: {}", inMessage);
                log.info("Creating new TestSession for InMessage...");

                // Create a objects using factory
                IncomingJmfMessage newMessage = TestRunner.getInstance().getTestSuite().createInMessage(inMessage.getContentType(), inMessage.getHeader(), inMessage.getBody(), true);
                testSession = TestRunner.getInstance().getTestSuite().createTestSession(remoteAddr);

                // Add TestSession to suite
                TestRunner.getInstance().getTestSuite().addTestSession(testSession);

                // Configure tests
                configurationHandler.configureIncomingTests(testSession);
                configurationHandler.configureOutgoingTests(testSession);

                // Add message to TestSession
                testSession.receiveMessage(newMessage);
            }
    }

}
