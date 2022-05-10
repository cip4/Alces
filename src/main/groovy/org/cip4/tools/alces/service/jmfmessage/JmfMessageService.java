package org.cip4.tools.alces.service.jmfmessage;

public interface JmfMessageService {

    /**
     * Returns the message type this implementation applies to.
     * @return The message type this implementation applies to.
     */
    String getMessageType();

    /**
     * Returns the buttons text extension shown on the trigger button.
     * @return The trigger button's text.
     */
    String getButtonTextExtension();

    /**
     * Create the JMF Message for the given type to be sent to the device.
     * @return The JMF Message to be sent to the device.
     */
    String createJmfMessage(IntegrationUtils integrationUtils);
}
