package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.tools.alces.service.discovery.model.MessageService;

public interface JmfMessageService {

    /**
     * Returns true in case the message service is accepted by the class..
     * @return true in case message service is accepted..
     */
    boolean accepts(MessageService messageService);

    /**
     * Returns the buttons text extension shown on the trigger button.
     * @return The trigger button's text.
     */
    String getButtonTextExtension();

    /**
     * Create the JMF Message for the given type to be sent to the device.
     * @return The JMF Message to be sent to the device.
     */
    String createJmfMessage(IntegrationUtils integrationUtils, StateInfo stateInfo);
}
