package org.cip4.tools.alces.service.discovery.model;

import java.util.Collections;
import java.util.List;

/**
 * Model object for a JDF Controller.
 */
public class JdfController {

    final List<JdfDevice> jdfDevices;

    final List<MessageService> messageServices;

    /**
     * Custom constructor.
     */
    public JdfController(List<JdfDevice> jdfDevices, List<MessageService> messageServices) {
        this.jdfDevices = Collections.unmodifiableList(jdfDevices);
        this.messageServices = Collections.unmodifiableList(messageServices);
    }

    public List<JdfDevice> getJdfDevices() {
        return jdfDevices;
    }

    public List<MessageService> getJdfMessageServices() {
        return messageServices;
    }
}
