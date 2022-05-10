package org.cip4.tools.alces.service.jmfmessage.old;

import java.io.File;

/**
 * Business interface encapsulating the JMF Message functionality.
 */
public interface JmfMessageService {

    /**
     * Create a query resource jmf message.
     * @return The QueryResource JMF Message.
     */
    String createResourceQuery();

    /**
     * Create a resource subscription jmf message.
     * @return The Resource Subscription JMF Message.
     */
    String createResourceSubscription();

    /**
     * Create a notification subscription jmf message.
     * @return The Notification Subscription JMF Message.
     */
    String createNotificationSubscription();

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
     * Create a query queue status jmf message.
     * @return The QueryQueueStatus JMF Message.
     */
    String createQueueStatusQuery();

    /**
     * Create a queue status subscription jmf message.
     * @return The QueueStatusSubscription JMF Message.
     */
    String createQueueStatusSubscription();

    /**
     * Create a stop persistent channel command.
     * @return The StopPersistentChannel JMF Command.
     */
    String createStopPersistentChannelCommand();

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
     * @param file The file to be submitted.
     * @return The SubmitQueueEntry JMF Message.
     */
    String createSubmitQueueEntry(File file);

    /**
     * Create a resubmit queue entry JMF command.
     * @param file The file to be resubmitted.
     * @return The ResubmitQueueEntry JMF Message.
     */
    String createResubmitQueueEntry(File file, String queueEntryId);

    /**
     * Create a suspend queue entry JMF command.
     * @param queueEntryId The queue entry id of the queue entry being suspended.
     * @return The SuspendQueueEntry JMF Message.
     */
    String createSuspendQueueEntry(String queueEntryId);

    /**
     * Create a resume queue entry JMF command.
     * @param queueEntryId The queue entry id of the queue entry being resumed.
     * @return The ResumeQueueEntry JMF Message.
     */
    String createResumeQueueEntry(String queueEntryId);

    /**
     * Create a abort queue entry JMF command.
     * @param queueEntryId The queue entry id of the queue entry being abort.
     * @return The AbortQueueEntry JMF Message.
     */
    String createAbortQueueEntry(String queueEntryId);

    /**
     * Create a hold queue entry JMF command.
     * @param queueEntryId The queue entry id of the queue entry being held.
     * @return The HoldQueueEntry JMF Message.
     */
    String createHoldQueueEntry(String queueEntryId);

    /**
     * Create a remove queue entry JMF command.
     * @param queueEntryId The queue entry id of the queue entry being removed.
     * @return The RemoveQueueEntry JMF Message.
     */
    String createRemoveQueueEntry(String queueEntryId);

    /**
     * Create a hold queue command.
     * @return The hold queue command.
     */
    String createHoldQueue();

    /**
     * Create a open queue command.
     * @return The open queue command.
     */
    String createOpenQueue();

    /**
     * Create a resume queue command.
     * @return The resume queue command.
     */
    String createResumeQueue();

    /**
     * Create a close queue command.
     * @return The close queue command.
     */
    String createCloseQueue();

    /**
     * Create a flush queue command.
     * @return The flush queue command.
     */
    String createFlushQueue();
}
