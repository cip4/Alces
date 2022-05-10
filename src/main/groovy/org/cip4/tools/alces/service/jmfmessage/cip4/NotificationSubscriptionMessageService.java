package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

@Service
public class NotificationSubscriptionMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "Notification";
    }

    @Override
    public String getButtonTextExtension() {
        return "Subscription";
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {
        return integrationUtils.getJmfBuilder()
                .buildNotificationSubscription(integrationUtils.getSubscriberUrl())
                .toXML();
    }
}
