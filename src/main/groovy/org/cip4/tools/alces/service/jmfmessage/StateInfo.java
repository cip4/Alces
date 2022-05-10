package org.cip4.tools.alces.service.jmfmessage;

public class StateInfo {

    private final String selectedQueueEntryId;

    public StateInfo(String selectedQueueEntryId) {
        this.selectedQueueEntryId = selectedQueueEntryId;
    }

    public String getSelectedQueueEntryId() {
        return selectedQueueEntryId;
    }
}
