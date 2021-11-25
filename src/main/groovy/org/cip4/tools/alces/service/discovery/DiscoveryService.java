package org.cip4.tools.alces.service.discovery;

import org.cip4.tools.alces.service.discovery.model.JdfController;

/**
 * Business interface encapsulating connectivity functionality.
 */
public interface DiscoveryService {

    /**
     * Discover a target jdf device or jdf controller.
     * @param jmfEndpointUrl The endpoint of the device/controller.
     * @return Model object containing discovered details.
     */
    JdfController discover(String jmfEndpointUrl);

}
