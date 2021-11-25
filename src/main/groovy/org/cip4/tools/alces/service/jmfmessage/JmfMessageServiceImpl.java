package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.jdflib.jmf.JMFBuilder;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.cip4.jdflib.auto.JDFAutoStatusQuParams.*;

/**
 * Implementation of the JmfMessageService interface.
 */
@Service
public class JmfMessageServiceImpl implements JmfMessageService {

    private static final String SENDER_ID = "ALCES";

    private final JMFBuilder jmfBuilder;

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
    public String createQueryStatus() {
        return jmfBuilder
                .buildStatus(EnumDeviceDetails.Details, EnumJobDetails.Brief)
                .toXML();
    }

    @Override
    public String createQueryQueueStatus() {
        return jmfBuilder
                .buildQueueStatus()
                .toXML();
    }

    @Override
    public String createQueryKnownDevices() {
        return jmfBuilder
                .buildKnownDevicesQuery(JDFAutoDeviceFilter.EnumDeviceDetails.Details)
                .toXML();
    }

    @Override
    public String createQueryKnownMessages() {
        return jmfBuilder
                .buildKnownMessagesQuery()
                .toXML();
    }
}
