package org.cip4.tools.alces.service.jmfmessage.cip4;

import org.cip4.tools.alces.service.discovery.model.MessageService;
import org.cip4.tools.alces.service.jmfmessage.IntegrationUtils;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.jmfmessage.StateInfo;
import org.springframework.stereotype.Service;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Objects;

@Service
public class ResubmitQueueEntryMessageService implements JmfMessageService {

    @Override
    public boolean accepts(MessageService messageService) {
        return Objects.equals(messageService.getType(), "ResubmitQueueEntry");
    }

    @Override
    public String getButtonTextExtension() {
        return null;
    }

    @Override
    public String createJmfMessage(IntegrationUtils integrationUtils, StateInfo stateInfo) {

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
                .buildResubmitQueueEntry(stateInfo.getSelectedQueueEntryId(), integrationUtils.publishFile(file))
                .toXML();
    }
}
