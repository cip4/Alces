package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.jdflib.jmf.JMFBuilder;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.tools.alces.service.file.FileService;
import org.cip4.tools.alces.service.settings.SettingsService;
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

    private static final String CONTEXT_PATH_FILES = "/alces/file/";

    private static final String CONTEXT_PATH_JMF = "/alces/jmf/";

    private final JMFBuilder jmfBuilder;

    @Autowired
    private FileService fileService;

    @Autowired
    private SettingsService settingsService;

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
    public String createStatusSubscription() {
        return jmfBuilder
                .buildStatusSubscription(getAlcesJmfUrl(), 0, 0, null)
                .toXML();
    }

    @Override
    public String createStopPersistentChannelCommand() {
        return jmfBuilder
                .buildStopPersistentChannel(null, null, getAlcesJmfUrl())
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
                .buildKnownSubscriptionsQuery(null, null)
                .toXML();
    }

    @Override
    public String createSubmitQueueEntry(File file) {

        // publish given file
        String filename = fileService.publishFile(file);

        // create and return message
        return jmfBuilder.
                buildSubmitQueueEntry(getAlcesJmfUrl(), getAlcesFileUrl(filename))
                .toXML();
    }

    /**
     * Helper method to provide Alces' JMF URL.
     * @return Alces confgured JMF URL
     */
    private String getAlcesJmfUrl() {
        return settingsService.getBaseUrl() + CONTEXT_PATH_JMF;
    }

    /**
     * Helper method to create a file url for a given file name.
     * @param filename The given file name.
     * @return The Alces' file url.
     */
    private String getAlcesFileUrl(String filename) {
        return settingsService.getBaseUrl() + CONTEXT_PATH_FILES + filename;
    }
}
