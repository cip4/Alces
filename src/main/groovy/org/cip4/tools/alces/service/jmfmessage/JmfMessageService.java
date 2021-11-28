package org.cip4.tools.alces.service.jmfmessage;

import java.io.File;

/**
 * Business interface encapsulating the JMF Message functionality.
 */
public interface JmfMessageService {

    /**
     * Create a query status jmf message.
     * @return The QueryStatus JMF Message.
     */
    String createStatusQuery();

    /**
     * Create a status subscription jmf message.
     * @return The Status Subscription JMF Message.
     */
    String createStatusSubscription();

    /**
     * Create a stop persistent channel command.
     * @return The StopPersistentChannel JMF Command.
     */
    String createStopPersistentChannelCommand();

    /**
     * Create a query queue status jmf message.
     * @return The QueryQueueStatus JMF Message.
     */
    String createQueueStatusQuery();

    /**
     * Create a query known devices jmf message.
     * @return The QueryKnownDevices JMF Message.
     */
    String createKnownDevicesQuery();

    /**
     * Create a query known messages jmf message.
     * @return The QueryKnownMessages JMF Message.
     */
    String createKnownMessagesQuery();

    /**
     * Create a query known subscriptions jmf message.
     * @return The QueryKnownSubscriptions JMF Message.
     */
    String createKnownSubscriptionsQuery();

    /**
     * Create a submit queue entry JMF command.
     * @param file The fle to be submitted.
     * @return The SubmitQueueEntry JMF Message.
     */
    String createSubmitQueueEntry(File file);
}
