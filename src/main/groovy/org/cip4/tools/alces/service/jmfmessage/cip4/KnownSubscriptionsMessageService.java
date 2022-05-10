package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

@Service
public class KnownSubscriptionsMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "KnownSubscriptions";
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {
        return integrationUtils.getJmfBuilder()
                .buildKnownSubscriptionsQuery(null, null)
                .toXML();
    }
}
