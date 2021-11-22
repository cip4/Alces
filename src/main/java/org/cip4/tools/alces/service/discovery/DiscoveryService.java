package org.cip4.tools.alces.service.discovery;

import org.cip4.tools.alces.service.discovery.model.Connection;

/**
 * Business interface encapsulating connectivity functionality.
 */
public interface DiscoveryService {

    /**
     * Initiate a connection to a target jmf device/controller.
     * @param jmfEndpointUrl The endpoint of the device/controller.
     * @return Model object containing discovered connection details.
     */
    Connection connect(String jmfEndpointUrl);

}
