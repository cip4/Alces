package org.cip4.tools.alces.service.jmfmessage;

import org.cip4.jdflib.jmf.JMFBuilder;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.tools.alces.service.settings.SettingsService;

public class IntegrationUtils {

    private final JMFBuilder jmfBuilder;

    private final SettingsService settingsService;

    /**
     * Custom constructor.
     */
    public IntegrationUtils(SettingsService settingsService) {
        this.settingsService = settingsService;

        // create and configure JMF Builder
        jmfBuilder = JMFBuilderFactory.getJMFBuilder(null);
        jmfBuilder.setAgentName(settingsService.getAgentName());
        jmfBuilder.setAgentVersion(settingsService.getAgentVersion());
        jmfBuilder.setSenderID(settingsService.getSenderId());
    }

    /**
     * Returns a pre-configured instance of the JDFLibJ JmfBuilder.
     * @return The JMF Builder
     */
    public JMFBuilder getJmfBuilder() {
        return jmfBuilder;
    }

    /**
     * Returns Alces' subscriber URL.
     * @return The subscriber URL of Alces.
     */
    public String getSubscriberUrl() {
        return settingsService.getBaseUrl() + "/alces/jmf/";
    }
}
