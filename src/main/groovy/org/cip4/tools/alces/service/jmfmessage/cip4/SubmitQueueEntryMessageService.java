package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

@Service
public class SubmitQueueEntryMessageService implements JmfMessageService {

    @Override
    public String getMessageType() {
        return "SubmitQueueEntry";
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils) {

        // define file filter
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".jdf")
                        || (file.isDirectory() && !file.getName().startsWith("."));
            }

            @Override
            public String getDescription() {
                return "JDF Job Tickets (*.jdf)";
            }
        };

        // select file to be submitted
        File file = integrationUtils.selectFile("Select JDF File", fileFilter);

        // create message
        return integrationUtils.getJmfBuilder()
                .buildSubmitQueueEntry(integrationUtils.getReturnUrl(), integrationUtils.publishFile(file))
                .toXML();
    }
}
