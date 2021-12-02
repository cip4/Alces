package org.cip4.tools.alces.service.discovery;

import org.cip4.tools.alces.service.discovery.model.*;

/**
 * Business interface encapsulating connectivity functionality.
 */
public interface DiscoveryService {

    /**
     * Register a new JdfControllerListener.
     * @param jdfControllerListener The JdfControlle lister.
     */
    void registerJdfControllerListener(JdfControllerListener jdfControllerListener);

    /**
     * Register a new QueueListener.
     * @param queueListener The queue lister.
     */
    void registerQueueListener(QueueListener queueListener);

    /**
     * Discover a target jdf device or jdf controller.
     * @param jmfEndpointUrl The endpoint of the device/controller.
     * @return Model object containing discovered details.
     */
    void discover(String jmfEndpointUrl);

    /**
     * Load the queue of an JDF Device.
     * @param jdfDevice The JDF Device.
     * @return Te queue of the JDF device.
     */
    void loadQueue(JdfDevice jdfDevice);
}
