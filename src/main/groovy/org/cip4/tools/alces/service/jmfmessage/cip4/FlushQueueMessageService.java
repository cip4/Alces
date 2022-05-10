package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.jdflib.jmf.JDFMessage;
import org.cip4.tools.alces.service.discovery.model.MessageService;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.jmfmessage.StateInfo;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FlushQueueMessageService implements JmfMessageService {

    @Override
    public boolean accepts(MessageService messageService) {
        return Objects.equals(messageService.getType(), "FlushQueue");
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils, StateInfo stateInfo) {
        return integrationUtils.getJmfBuilder()
                .createJMF(JDFMessage.EnumFamily.Command, JDFMessage.EnumType.FlushQueue)
                .toXML();
    }
}
