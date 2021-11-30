package org.cip4.tools.alces.service.discovery.model;

public class QueueEntry {

    private final String queueEntryId;
    private final String jobId;
    private final String jobPartId;
    private final int priority;
    private final String status;

    /**
     * Custom constructor. Accepting multiple parameters for initializing.
     */
    public QueueEntry(String queueEntryId, String jobId, String jobPartId, int priority, String status) {
        this.queueEntryId = queueEntryId;
        this.jobId = jobId;
        this.jobPartId = jobPartId;
        this.priority = priority;
        this.status = status;
    }

    public String getQueueEntryId() {
        return queueEntryId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobPartId() {
        return jobPartId;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }
}
