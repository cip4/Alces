package org.cip4.tools.alces.service.discovery.model;

/**
 * Model object of a jdf message service.
 */
public class MessageService {

    private final String type;
    private final String urlSchemes;

    private final boolean acknowledge;
    private final boolean command;
    private final boolean query;
    private final boolean registration;
    private final boolean signal;

    /**
     * Custom constructor. Accepting multiple params for initializing.
     */
    public MessageService(String type, String urlSchemes, boolean acknowledge, boolean command, boolean query,
                          boolean registration, boolean signal) {
        this.type = type;
        this.urlSchemes = urlSchemes;

        this.acknowledge = acknowledge;
        this.command = command;
        this.query = query;
        this.registration = registration;
        this.signal = signal;
    }

    public String getType() {
        return type;
    }

    public String getUrlSchemes() {
        return urlSchemes;
    }

    public boolean isAcknowledge() {
        return acknowledge;
    }

    public boolean isCommand() {
        return command;
    }

    public boolean isQuery() {
        return query;
    }

    public boolean isRegistration() {
        return registration;
    }

    public boolean isSignal() {
        return signal;
    }
}
