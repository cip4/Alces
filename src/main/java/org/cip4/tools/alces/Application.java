package org.cip4.tools.alces;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.cip4.tools.alces.service.about.AboutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
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

    private static ConfigurableApplicationContext applicationContext;

    @Autowired
    private AboutService aboutService;

    /**
     * Applications main entrance point.
     * @param args Applications parameter.
     */
    public static void main(String[] args) throws Exception {

        // set theme
        try {
            UIManager.setLookAndFeel( new FlatIntelliJLaf() );
        } catch( Exception ex ) {
            log.error("Failed to initialize LaF", ex);
        }

        // start spring app
        applicationContext = new SpringApplicationBuilder(Application.class).headless(false).run(args);

    }

    /**
     * Shutdown Alces.
     */
    public static void initiateShutdown() {
        SpringApplication.exit(applicationContext, () -> 0);
        System.exit(0);
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
