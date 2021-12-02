package org.cip4.tools.alces.service.discovery.model;

/**
 * Listener interface for a JdfController updates.
 */
public interface JdfControllerListener {

    /**
     * Handling of a JdfController updtae.
     * @param jdfController The new jdfController object.
     */
    void handleJdfControllerUpdate(JdfController jdfController);

}
