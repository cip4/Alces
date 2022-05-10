package org.cip4.tools.alces.service.jmfmessage.cpp;

import org.cip4.jdflib.auto.JDFAutoStatusQuParams;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

@Service
public class AssignMediaToTrayMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "oce:AssignMediaToTray";
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
