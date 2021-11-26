package org.cip4.tools.alces.service.discovery.model;

import java.util.List;

/**
 * Model object for a JDF Controller.
 */
public class JdfController {

    final List<JdfDevice> jdfDevices;

    final List<JdfMessageService> jdfMessageServices;

    /**
     * Custom constructor.
     */
    public JdfController(List<JdfDevice> jdfDevices, List<JdfMessageService> jdfMessageServices) {
        this.jdfDevices = jdfDevices;
        this.jdfMessageServices = jdfMessageServices;
    }

    public List<JdfDevice> getJdfDevices() {
        return jdfDevices;
    }

    public List<JdfMessageService> getJdfMessageServices() {
        return jdfMessageServices;
    }
}
