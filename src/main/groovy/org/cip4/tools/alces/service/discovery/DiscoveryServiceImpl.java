package org.cip4.tools.alces.service.discovery;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessageService;
import org.cip4.jdflib.jmf.JDFResponse;
import org.cip4.jdflib.resource.JDFDevice;
import org.cip4.jdflib.resource.JDFDeviceList;
import org.cip4.tools.alces.service.discovery.model.JdfController;
import org.cip4.tools.alces.service.discovery.model.JdfDevice;
import org.cip4.tools.alces.service.discovery.model.JdfMessageService;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.settings.SettingsServiceImpl;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.cip4.tools.alces.service.testrunner.model.IncomingJmfMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the DiscoveryService interface.
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    private static Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    @Autowired
    private JmfMessageService jmfMessageService;

    @Autowired
    private TestRunnerService testRunnerService;

    @Override
    public JdfController discover(String jmfEndpointUrl) {
        log.info("Discover JMF Endpoint '{}'", jmfEndpointUrl);

        // initiate JMF Handshake
        List<JdfDevice> jdfDevices = processKnownDevices(jmfEndpointUrl);
        List<JdfMessageService> jdfMessageServices = processKnownMessages(jmfEndpointUrl);
        // TODO: Queue Status

        // create and return controller object
        return new JdfController(
                Collections.unmodifiableList(jdfDevices),
                Collections.unmodifiableList(jdfMessageServices)
        );
    }

    /**
     * Process a known messages message.
     * @param jmfEndpointUrl The target url.
     * @return List of JDF Message Services.
     */
    private List<JdfMessageService> processKnownMessages(String jmfEndpointUrl) {

        // query target url
        String jmfResponseKnownDevices = testRunnerService
                .startTestSession(jmfMessageService.createKnownMessagesQuery(), jmfEndpointUrl)
                .getIncomingJmfMessages().get(0)
                .getBody();

        // analyze response
        JDFDoc jdfDoc = JDFDoc.parseStream(new ByteArrayInputStream(jmfResponseKnownDevices.getBytes(StandardCharsets.UTF_8)));
        JDFResponse jdfResponse = jdfDoc.getJMFRoot().getResponse(0);

        List<JdfMessageService> jdfMessageServices = new ArrayList<>();

        if (jdfResponse != null) {
            Arrays.stream(jdfResponse.getChildElementArray()).forEach(kElement -> {

                JDFMessageService jdfMessageService = (JDFMessageService) kElement;

                jdfMessageServices.add(
                        new JdfMessageService(
                                jdfMessageService.getType(),
                                jdfMessageService.getAttribute(AttributeName.URLSCHEMES)
                        )
                );
            });
        }

        // return device list
        return jdfMessageServices;
    }

    /**
     * Process a known devices message.
     * @param jmfEndpointUrl The target url.
     * @return List of JDF Devices.
     */
    private List<JdfDevice> processKnownDevices(String jmfEndpointUrl) {

        // query target url
        String jmfResponseKnownDevices = testRunnerService
                .startTestSession(jmfMessageService.createKnownDevicesQuery(), jmfEndpointUrl)
                .getIncomingJmfMessages().get(0)
                .getBody();

        // analyze response
        JDFDoc jdfDoc = JDFDoc.parseStream(new ByteArrayInputStream(jmfResponseKnownDevices.getBytes(StandardCharsets.UTF_8)));
        JDFDeviceList deviceList = jdfDoc.getJMFRoot().getResponse(0).getDeviceList(0);

        List<JdfDevice> jdfDevices = new ArrayList<>();

        if (deviceList != null) {
            deviceList.getAllDeviceInfo().stream().forEach(jdfDeviceInfo -> {

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
}
