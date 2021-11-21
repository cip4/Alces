package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.core.JDFAudit;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.jmf.JMFBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.cip4.jdflib.auto.JDFAutoStatusQuParams.*;

/**
 * Implementation of the JmfMessageService interface.
 */
@Service
public class JmfMessageServiceImpl implements JmfMessageService {

    private final String senderId = "ALCES";

    @Value("${app.name}")
    private String agentName;

    @Value("${app.version}")
    private String agentVersion;

    @PostConstruct
    public void init() {
        JDFAudit.setStaticAgentName(agentName);
        JDFAudit.setStaticAgentVersion(agentVersion);
        JDFAudit.setStaticAuthor("Chuck Norris");
    }

    @Override
    public String createQueryStatus() {
        return new JMFBuilder()
                .buildStatus(EnumDeviceDetails.Details, EnumJobDetails.Brief)
                .toXML();
    }
}
