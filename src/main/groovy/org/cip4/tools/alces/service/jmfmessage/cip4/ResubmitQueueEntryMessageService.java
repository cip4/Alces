package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

import javax.swing.filechooser.FileFilter;
import java.io.File;

@Service
public class ResubmitQueueEntryMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "ResubmitQueueEntry";
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {

        return "";
    }
}
