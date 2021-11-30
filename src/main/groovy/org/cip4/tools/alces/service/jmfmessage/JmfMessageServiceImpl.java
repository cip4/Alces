package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.jdflib.jmf.JDFMessage;
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
    public String createResourceQuery() {
        return jmfBuilder
                .buildResourceQuery(true)
                .toXML();
    }

    @Override
    public String createResourceSubscription() {
        return jmfBuilder
                .buildResourceSubscription(getAlcesJmfUrl(), 0, 0, null)
                .toXML();
    }

    @Override
    public String createNotificationSubscription() {
        return jmfBuilder
                .buildNotificationSubscription(getAlcesJmfUrl())
                .toXML();
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
    public String createQueueStatusSubscription() {
        return jmfBuilder
                .buildQueueStatusSubscription(getAlcesJmfUrl())
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
        return jmfBuilder
                .buildSubmitQueueEntry(getAlcesJmfUrl(), getAlcesFileUrl(filename))
                .toXML();
    }

    @Override
    public String createResubmitQueueEntry(File file, String queueEntryId) {

        // publish given file
        String filename = fileService.publishFile(file);

        // create and return message
        return jmfBuilder
                .buildResubmitQueueEntry(queueEntryId, getAlcesFileUrl(filename))
                .toXML();
    }

    @Override
    public String createSuspendQueueEntry(String queueEntryId) {

        // create and return message
        return jmfBuilder
                .buildSuspendQueueEntry(queueEntryId)
                .toXML();
    }

    @Override
    public String createResumeQueueEntry(String queueEntryId) {

        // create and return message
        return jmfBuilder
                .buildResumeQueueEntry(queueEntryId)
                .toXML();
    }

    @Override
    public String createAbortQueueEntry(String queueEntryId) {

        // create and return message
        return jmfBuilder
                .buildAbortQueueEntry(queueEntryId)
                .toXML();
    }

    @Override
    public String createHoldQueueEntry(String queueEntryId) {

        // create and return message
        return jmfBuilder
                .buildHoldQueueEntry(queueEntryId)
                .toXML();
    }

    @Override
    public String createRemoveQueueEntry(String queueEntryId) {

        // create and return message
        return jmfBuilder
                .buildRemoveQueueEntry(queueEntryId)
                .toXML();
    }

    @Override
    public String createHoldQueue() {
        return jmfBuilder
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.HoldQueue)
                .toXML();
    }

    @Override
    public String createOpenQueue() {
        return jmfBuilder
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.OpenQueue)
                .toXML();
    }

    @Override
    public String createResumeQueue() {
        return jmfBuilder
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.ResumeQueue)
                .toXML();
    }

    @Override
    public String createCloseQueue() {
        return jmfBuilder
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.CloseQueue)
                .toXML();
    }

    @Override
    public String createFlushQueue() {
        return jmfBuilder
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.FlushQueue)
                .toXML();
    }

    /**
     * Helper method to provide Alces' JMF URL.
     * @return Alces confgured JMF URL
     */
    private String getAlcesJmfUrl() {
        return settingsService.getBaseUrl() + "/alces/jmf/";
    }

    /**
     * Helper method to create a file url for a given file name.
     * @param filename The given file name.
     * @return The Alces' file url.
     */
    private String getAlcesFileUrl(String filename) {
        return settingsService.getBaseUrl() + "/alces/file/" + filename;
    }
}
