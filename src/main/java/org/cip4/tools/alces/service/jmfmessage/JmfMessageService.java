package org.cip4.tools.alces.service.jmfmessage;

/**
 * Business interface encapsulating the JMF Message functionality.
 */
public interface JmfMessageService {

    /**
     * Create a query status jmf message.
     * @return The QueryStatus JMF Message.
     */
    String createQueryStatus();

    /**
     * Create a query queue status jmf message.
     * @return The QueryQueueStatus JMF Message.
     */
    String createQueryQueueStatus();

    /**
     * Create a query known devices jmf message.
     * @return The QueryKnownDevices JMF Message.
     */
    String createQueryKnownDevices();

    /**
     * Create a query known messages jmf message.
     * @return The QueryKnownMessages JMF Message.
     */
    String createQueryKnownMessages();
}
