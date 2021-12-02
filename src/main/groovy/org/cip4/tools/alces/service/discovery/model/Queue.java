package org.cip4.tools.alces.service.discovery.model;

import java.util.Collections;
import java.util.List;

public class Queue {

    private final String deviceId;
    private final String status;
    private final long lastUpdated;
    private final List<QueueEntry> queueEntries;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     */
    public Queue(String deviceId, String status, long lastUpdated, List<QueueEntry> queueEntries) {
        this.deviceId = deviceId;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.queueEntries = Collections.unmodifiableList(queueEntries);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getStatus() {
        return status;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public List<QueueEntry> getQueueEntries() {
        return queueEntries;
    }
}
