package org.cip4.tools.alces.service.discovery.model;

import java.util.List;
import java.util.Queue;

/**
 * Model object for a JDF Device.
 */
public class JdfDevice {

    private final String deviceId;
    private final String descriptiveName;
    private final String jmfSenderId;
    private final String jmfUrl;
    private final String jdfVersions;
    private final String deviceType;
    private final String manufacturer;
    private final String modelName;
    private final String modelNumber;
    private final String agentName;
    private final String agentVersion;
    private final String icsVerions;
    private final List<QueueEntry> queueEntries;

    /**
     * Default constructor.
     */
    private JdfDevice(Builder builder) {
        this.deviceId = builder.deviceId;
        this.descriptiveName = builder.descriptiveName;
        this.jmfSenderId = builder.jmfSenderId;
        this.jmfUrl = builder.jmfUrl;
        this.jdfVersions = builder.jdfVersions;
        this.deviceType = builder.deviceType;
        this.manufacturer = builder.manufacturer;
        this.modelName = builder.modelName;
        this.modelNumber = builder.modelNumber;
        this.agentName = builder.agentName;
        this.agentVersion = builder.agentVersion;
        this.icsVerions = builder.icsVerions;
        this.queueEntries = builder.queueEntries;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public String getJmfSenderId() {
        return jmfSenderId;
    }

    public String getJmfUrl() {
        return jmfUrl;
    }

    public String getJdfVersions() {
        return jdfVersions;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public String getIcsVerions() {
        return icsVerions;
    }

    public List<QueueEntry> getQueueEntries() {
        return queueEntries;
    }

    /**
     * Device builder class
     */
    public static class Builder {

        private String deviceId;
        private String descriptiveName;
        private String jmfSenderId;
        private String jmfUrl;
        private String jdfVersions;
        private String deviceType;
        private String manufacturer;
        private String modelName;
        private String modelNumber;
        private String agentName;
        private String agentVersion;
        private String icsVerions;
        private List<QueueEntry> queueEntries;

        /**
         * Default constructor.
         */
        public Builder() {
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withDescriptiveName(String descriptiveName) {
            this.descriptiveName = descriptiveName;
            return this;
        }

        public Builder withJmfSenderId(String jmfSenderId) {
            this.jmfSenderId = jmfSenderId;
            return this;
        }

        public Builder withJmfUrl(String jmfUrl) {
            this.jmfUrl = jmfUrl;
            return this;
        }

        public Builder withJdfVersions(String jdfVersions) {
            this.jdfVersions = jdfVersions;
            return this;
        }

        public Builder withDeviceType(String deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder withManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder withModelNumber(String modelNumber) {
            this.modelNumber = modelNumber;
            return this;
        }

        public Builder withAgentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public Builder withAgentVersion(String agentVersion) {
            this.agentVersion = agentVersion;
            return this;
        }

        public Builder withIcsVerions(String icsVerions) {
            this.icsVerions = icsVerions;
            return this;
        }

        public Builder withQueueEntries(List<QueueEntry> queueEntries) {
            this.queueEntries = queueEntries;
            return this;
        }

        public JdfDevice build() {
            return new JdfDevice(this);
        }
    }
}
