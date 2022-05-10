package org.cip4.tools.alces.service.discovery;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFMessageService;
import org.cip4.jdflib.jmf.JDFQueue;
import org.cip4.jdflib.jmf.JDFResponse;
import org.cip4.jdflib.resource.JDFDeviceList;
import org.cip4.tools.alces.service.discovery.model.*;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.settings.SettingsService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of the DiscoveryService interface.
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    private static Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    private List<JdfControllerListener> jdfControllerListeners = new ArrayList<>();
    private List<QueueListener> queueListeners = new ArrayList<>();

    @Autowired
    @Qualifier("knownMessagesMessageService")
    private JmfMessageService knownMessagesMessageService;

    @Autowired
    @Qualifier("knownDevicesMessageService")
    private JmfMessageService knownDevicesMessageService;

    @Autowired
    @Qualifier("queueStatusMessageService")
    private JmfMessageService queueStatusMessageService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TestRunnerService testRunnerService;

    @Override
    public void registerJdfControllerListener(JdfControllerListener jdfControllerListener) {
        jdfControllerListeners.add(jdfControllerListener);
    }

    @Override
    public void registerQueueListener(QueueListener queueListener) {
        queueListeners.add(queueListener);
    }

    @Override
    @Async
    public void discover(String jmfEndpointUrl) {
        log.info("Discover JMF Endpoint '{}'", jmfEndpointUrl);

        // initiate JMF Handshake
        List<JdfDevice> jdfDevices;

        try {
            jdfDevices = processKnownDevices(jmfEndpointUrl);
        } catch (Exception e) {
            log.error("Error reading JDF Devices.", e);
            jdfDevices = null;
        }

        List<MessageService> messageServices;

        try {
            messageServices = processKnownMessages(jmfEndpointUrl);
        } catch (Exception e) {
            log.error("Error reading JDF Message Services.", e);
            messageServices = null;
        }

        JdfController jdfController = new JdfController(jdfDevices, messageServices);

        // notify listener
        jdfControllerListeners.forEach(jdfControllerListener -> jdfControllerListener.handleJdfControllerUpdate(jdfController));
    }

    @Override
    @Async
    public void loadQueue(JdfDevice jdfDevice) {
        final Queue queue;
        Queue queue1;

        try {
            queue1 = processQueueStatus(jdfDevice.getJmfUrl());
        } catch (Exception e) {
            log.error("Error reading Queue Status.", e);
            queue1 = null;
        }

        // notify listeners
        queue = queue1;
        queueListeners.forEach(queueListener -> queueListener.handleQueueUpdate(queue));
    }

    /**
     * Process a known messages message.
     *
     * @param jmfEndpointUrl The target url.
     * @return List of JDF Message Services.
     */
    private List<MessageService> processKnownMessages(String jmfEndpointUrl) throws ExecutionException, InterruptedException {

        // prepare integration utils
        IntegrationUtils integrationUtils = new IntegrationUtils(settingsService, null, null);

        // query target url
        String jmfResponseKnownDevices = testRunnerService
                .startTestSession(knownMessagesMessageService.createJmfMessage(integrationUtils, null), jmfEndpointUrl).get()
                .getIncomingJmfMessages().get(0)
                .getBody();

        // analyze response
        JDFDoc jdfDoc = JDFDoc.parseStream(new ByteArrayInputStream(jmfResponseKnownDevices.getBytes(StandardCharsets.UTF_8)));
        JDFResponse jdfResponse = jdfDoc.getJMFRoot().getResponse(0);

        List<MessageService> messageServices = new ArrayList<>();

        if (jdfResponse != null) {
            Arrays.stream(jdfResponse.getChildElementArray()).forEach(kElement -> {

                JDFMessageService jdfMessageService = (JDFMessageService) kElement;

                messageServices.add(
                        new MessageService(
                                jdfMessageService.getType(),
                                jdfMessageService.getAttribute(AttributeName.URLSCHEMES),
                                jdfMessageService.getAcknowledge(),
                                jdfMessageService.getCommand(),
                                jdfMessageService.getQuery(),
                                jdfMessageService.getRegistration(),
                                jdfMessageService.getSignal()
                        )
                );
            });
        }

        // return device list
        return messageServices;
    }

    /**
     * Process a known devices message.
     *
     * @param jmfEndpointUrl The target url.
     * @return List of JDF Devices.
     */
    private List<JdfDevice> processKnownDevices(String jmfEndpointUrl) throws ExecutionException, InterruptedException {

        // prepare integration utils
        IntegrationUtils integrationUtils = new IntegrationUtils(settingsService, null, null);

        // query target url
        String jmfResponseKnownDevices = testRunnerService
                .startTestSession(knownDevicesMessageService.createJmfMessage(integrationUtils, null), jmfEndpointUrl).get()
                .getIncomingJmfMessages().get(0)
                .getBody();

        // analyze response
        JDFDoc jdfDoc = JDFDoc.parseStream(new ByteArrayInputStream(jmfResponseKnownDevices.getBytes(StandardCharsets.UTF_8)));
        JDFDeviceList deviceList = jdfDoc.getJMFRoot().getResponse(0).getDeviceList(0);

        List<JdfDevice> jdfDevices = new ArrayList<>();

        if (deviceList != null) {
            deviceList.getAllDeviceInfo().forEach(jdfDeviceInfo -> {

                // build jdf device
                JdfDevice jdfDevice = new JdfDevice.Builder()
                        .withDeviceId(jdfDeviceInfo.getDeviceID() == null ? jdfDeviceInfo.getDevice().getDeviceID() : jdfDeviceInfo.getDeviceID())
                        .withJmfSenderId(jdfDeviceInfo.getDevice().getJMFSenderID())
                        .withJmfUrl(jdfDeviceInfo.getDevice().getJMFURL())
                        .withJdfVersions(jdfDeviceInfo.getDevice().getJDFVersions())
                        .withIcsVerions(jdfDeviceInfo.getDevice().getAttribute(AttributeName.ICSVERSIONS))
                        .withDescriptiveName(jdfDeviceInfo.getDevice().getDescriptiveName())
                        .withAgentName(jdfDeviceInfo.getDevice().getAgentName())
                        .withAgentVersion(jdfDeviceInfo.getDevice().getAgentVersion())
                        .withDeviceType(jdfDeviceInfo.getDevice().getDeviceType())
                        .withManufacturer(jdfDeviceInfo.getDevice().getManufacturer())
                        .withModelName(jdfDeviceInfo.getDevice().getModelName())
                        .withModelNumber(jdfDeviceInfo.getDevice().getModelNumber())
                        .build();

                // add device to list
                jdfDevices.add(jdfDevice);
            });
        }

        // return device list
        return jdfDevices;
    }

    /**
     * Process a queue status message.
     *
     * @param jmfEndpointUrl The target url.
     * @return List of QueueEntry objects.
     */
    private Queue processQueueStatus(String jmfEndpointUrl) throws ExecutionException, InterruptedException {

        // prepare integration utils
        IntegrationUtils integrationUtils = new IntegrationUtils(settingsService, null, null);

        // query target url
        String jmfResponseQueueStatus = testRunnerService
                .startTestSession(queueStatusMessageService.createJmfMessage(integrationUtils, null), jmfEndpointUrl).get()
                .getIncomingJmfMessages().get(0)
                .getBody();

        // analyze response
        JDFDoc jdfDoc = JDFDoc.parseStream(new ByteArrayInputStream(jmfResponseQueueStatus.getBytes(StandardCharsets.UTF_8)));
        JDFQueue jdfQueue = jdfDoc.getJMFRoot().getResponse(0).getQueue(0);

        // extract queue entries
        Queue queue;

        if (jdfQueue != null) {
            List<QueueEntry> queueEntries = new ArrayList<>();

            jdfQueue.getAllQueueEntry().forEach(jdfQueueEntry -> {

                // build queue entry
                QueueEntry queueEntry = new QueueEntry(
                        jdfQueueEntry.getQueueEntryID(),
                        jdfQueueEntry.getJobID(),
                        jdfQueueEntry.getJobPartID(),
                        jdfQueueEntry.getPriority(),
                        jdfQueueEntry.getQueueEntryStatus().getName()
                );

                // add queue entry to list
                queueEntries.add(queueEntry);
            });

            // extract queue details
            queue = new Queue(
                    jdfQueue.getDeviceID(),
                    jdfQueue.getQueueStatus().getName(),
                    System.currentTimeMillis(),
                    queueEntries
            );
        } else {
            queue = null;
        }

        // return queue
        return queue;
    }
}
