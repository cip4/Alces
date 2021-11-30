package org.cip4.tools.alces.service.discovery.model;

/**
 * Model object of a jdf message service.
 */
public class MessageService {

    private final String type;
    private final String urlSchemes;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     */
    public MessageService(String type, String urlSchemes) {
        this.type = type;
        this.urlSchemes = urlSchemes;
    }

    public String getType() {
        return type;
    }

    public String getUrlSchemes() {
        return urlSchemes;
    }
}
