package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

@Service
public class KnownDevicesMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "KnownDevices";
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {
        return integrationUtils.getJmfBuilder()
                .buildKnownDevicesQuery(JDFAutoDeviceFilter.EnumDeviceDetails.Details)
                .toXML();
    }
}
