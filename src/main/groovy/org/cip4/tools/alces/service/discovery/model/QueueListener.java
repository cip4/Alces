package org.cip4.tools.alces.service.discovery.model;

/**
 * Listener interface for a Queue changes.
 */
public interface QueueListener {

    /**
     * Handling of a Queue update.
     * @param queue The new Queue object.
     */
    void handleQueueUpdate(Queue queue);
}
