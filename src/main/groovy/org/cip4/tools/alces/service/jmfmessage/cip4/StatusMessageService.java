package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.jdflib.auto.JDFAutoStatusQuParams;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

@Service
public class StatusMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "Status";
    }

    @Override
    public String getButtonTextExtension() {
        return "";
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {
        return integrationUtils.getJmfBuilder()
                .buildStatus(JDFAutoStatusQuParams.EnumDeviceDetails.Details, JDFAutoStatusQuParams.EnumJobDetails.Brief)
                .toXML();
    }
}
