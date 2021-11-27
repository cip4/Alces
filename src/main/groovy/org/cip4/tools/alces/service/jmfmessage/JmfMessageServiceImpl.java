package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.jdflib.jmf.JMFBuilder;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.tools.alces.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.File;

import static org.cip4.jdflib.auto.JDFAutoStatusQuParams.*;

/**
 * Implementation of the JmfMessageService interface.
 */
@Service
public class JmfMessageServiceImpl implements JmfMessageService {

    private static final String SENDER_ID = "ALCES";

    private final JMFBuilder jmfBuilder;

    @Autowired
    private FileService fileService;

    @Value("${app.name}")
    private String agentName;

    @Value("${app.version}")
    private String agentVersion;

    /**
     * Default constructor.
     */
    public JmfMessageServiceImpl() {
        jmfBuilder = JMFBuilderFactory.getJMFBuilder(null);
    }

    @PostConstruct
    public void init() {
        jmfBuilder.setAgentName(agentName);
        jmfBuilder.setAgentVersion(agentVersion);
        jmfBuilder.setSenderID(SENDER_ID);
    }

    @Override
    public String createStatusQuery() {
        return jmfBuilder
                .buildStatus(EnumDeviceDetails.Details, EnumJobDetails.Brief)
                .toXML();
    }

    @Override
    public String createStatusSubscription(String subscriberUrl) {
        return jmfBuilder
                .buildStatusSubscription(subscriberUrl, 0, 0, null)
                .toXML();
    }

    @Override
    public String createStopPersistentChannelCommand(String subscriberUrl) {
        return jmfBuilder
                .buildStopPersistentChannel(null, null, subscriberUrl)
                .toXML();
    }

    @Override
    public String createQueueStatusQuery() {
        return jmfBuilder
                .buildQueueStatus()
                .toXML();
    }

    @Override
    public String createKnownDevicesQuery() {
        return jmfBuilder
                .buildKnownDevicesQuery(JDFAutoDeviceFilter.EnumDeviceDetails.Details)
                .toXML();
    }

    @Override
    public String createKnownMessagesQuery() {
        return jmfBuilder
                .buildKnownMessagesQuery()
                .toXML();
    }

    @Override
    public String createKnownSubscriptionsQuery() {
        return jmfBuilder
                .buildKnownMessagesQuery()
                .toXML();
    }

    @Override
    public String createSubmitQueueEntry(File file) {
        fileService.publishFile(file);

        return "";
    }
}
