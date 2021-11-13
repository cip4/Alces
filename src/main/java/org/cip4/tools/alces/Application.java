package org.cip4.tools.alces;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.cip4.tools.alces.swingui.Alces;
import org.cip4.tools.alces.service.AboutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

/**
 * Applications main class.
 */
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private AboutService aboutService;

    /**
     * Applications main entrance point.
     * @param args Applications parameter.
     */
    public static void main(String[] args) throws Exception {

        // set theme
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        // start spring app
        new SpringApplicationBuilder(Application.class).headless(false).run(args);

        // start swing ui
        Alces alces = new Alces();
        alces.setVisible(true);
    }

    /**
     * Create a RestTemplate bean for sending messages via http.
     * @param builder The RestTemplateBuilder object.
     * @return The created RestBuilder object.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Event is called after applications start up.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartUp() {
        log.warn(String.format("%s %s has started. (buildtime: %s)", aboutService.getAppName(), aboutService.getAppVersion(), aboutService.getBuildTime()));
    }
}
