package org.cip4.tools.alces.controller;

import org.apache.commons.io.FilenameUtils;
import org.cip4.jdflib.core.JDFComment;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.jdflib.jmf.JDFResponse;
import org.cip4.jdflib.resource.JDFNotification;
import org.cip4.tools.alces.service.file.FileService;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.util.JmfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

@RestController
public class JmfController {

    private static final Logger log = LoggerFactory.getLogger(JmfController.class);

    private static final String JMF_CONTENT_TYPE = "application/vnd.cip4-jmf+xml";
    private static final String JDF_CONTENT_TYPE = "application/vnd.cip4-jdf+xml";
    private static final String MIME_CONTENT_TYPE = "multipart/related";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TestRunnerService testRunnerService;

    @RequestMapping(value = "/alces/file/{filename}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> loadFle(@PathVariable String filename) throws IOException {
        log.info("Load file '{}'", filename);

        File file = fileService.getPublishedFile(filename);

        // check if file exists
        if(file.exists()) {

            // extract extension
            String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();

            // define content type
            String contentType = switch (extension) {
                case "jdf" -> JDFConstants.MIME_JDF;
                case "ppf" -> JDFConstants.MIME_CIP3;
                default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
            };

            // return file
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", contentType);
            headers.add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            return new ResponseEntity<>(
                    Files.readAllBytes(file.toPath()),
                    headers,
                    HttpStatus.OK
            );

        } else {

            // return 404 error
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

        final IncomingJmfMessage incomingJmfMessage = new IncomingJmfMessage(contentType, messageBody);

        // create and send response
        final JDFJMF jmfIn = JmfUtil.getBodyAsJMF(incomingJmfMessage);
        ResponseEntity<String> responseEntity;

        if (jmfIn != null) {
            if (jmfIn.getAcknowledge(0) != null) {
                log.debug("Receiving Acknowledge message...");
                testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
                responseEntity = ResponseEntity.ok().build();

            } else if (jmfIn.getSignal(0) != null) {
                log.debug("Receiving Signal message...");
                testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
                responseEntity = ResponseEntity.ok().build();

            } else if (jmfIn.getMessageElement(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.ReturnQueueEntry, 0) != null) {
                log.debug("Receiving ReturnQueueEntry message...");
                testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
                responseEntity = ResponseEntity.ok(jmfIn.createResponse().toXML());

            } else {
                log.debug("Receiving unhandled JMF message...");
                testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);

                JDFJMF jdfResponse = jmfIn.createResponse();
                jdfResponse.getResponse(0).setReturnCode(5);

                jdfResponse.getResponse(0)
                        .appendNotification()
                        .appendComment()
                        .setText("Alces has received and logged your messages but does not know how to process the message.");

                responseEntity = ResponseEntity.ok(jdfResponse.toXML());
            }


        } else if (contentType.equals(MIME_CONTENT_TYPE)) {
            log.debug("Receiving MIME package...");
            testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
            responseEntity = ResponseEntity.ok().build();

        } else if (contentType.startsWith(JDF_CONTENT_TYPE)) {
            log.debug("Receiving JDF file...");
            testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
            responseEntity = ResponseEntity.ok().build();

        } else {
            log.debug("Unknown content-type '" + contentType + "'...");
            testRunnerService.processIncomingJmfMessage(incomingJmfMessage, remoteAddr);
            responseEntity = ResponseEntity.badRequest().build();
        }

        return responseEntity;
    }
}
