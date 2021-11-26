package org.cip4.tools.alces.service.discovery.model;

import java.util.List;

/**
 * Model object for a JDF Controller.
 */
public class JdfController {

    List<JdfDevice> jdfDevices;

    /**
     * Default constructor.
     */
    public JdfController() {

    }

    public List<JdfDevice> getJdfDevices() {
        return jdfDevices;
    }

    public void setJdfDevices(List<JdfDevice> jdfDevices) {
        this.jdfDevices = jdfDevices;
    }
}
