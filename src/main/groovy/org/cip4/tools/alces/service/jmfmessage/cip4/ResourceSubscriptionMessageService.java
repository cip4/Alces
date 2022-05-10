package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.tools.alces.service.discovery.model.MessageService;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.jmfmessage.StateInfo;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ResourceSubscriptionMessageService implements JmfMessageService {

    @Override
    public boolean accepts(MessageService messageService) {
        return Objects.equals(messageService.getType(), "Resource");
    }

    @Override
    public String getButtonTextExtension() {
        return "Subscription";
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils, StateInfo stateInfo) {
        return integrationUtils.getJmfBuilder()
                .buildResourceSubscription(integrationUtils.getSubscriberUrl(), 0, 0, null)
                .toXML();
    }
}
