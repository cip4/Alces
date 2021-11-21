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
}
