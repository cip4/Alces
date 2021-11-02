package org.cip4.tools.alces.service;

import org.cip4.jdflib.core.JDFAudit;
import org.cip4.tools.alces.util.JDFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implementation of the about service interface.
 */
@Service
public class AboutServiceImpl implements AboutService {

    private static final Logger log = LoggerFactory.getLogger(AboutServiceImpl.class);

    private static final long startTime = System.currentTimeMillis();

    private static final String hostname = readHostname();

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String version;

    @Value("${app.buildtime}")
    private String buildTime;

    /**
     * Default constructor.
     */
    public AboutServiceImpl() {
    }


    /**
     * Read and returns the system's hostname.
     * @return The system's hostname as string.
     */
    private static String readHostname() {
        String hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "UNKNOWN";
        }

        return hostname;
    }


    @Override
    public String getAppVersion() {
        return version.trim();
    }

    @Override
    public String getAppName() {
        return appName.trim();
    }

    @Override
    public String getBuildTime() {
        return buildTime.trim();
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getJdfLibJVersion() {
        return JDFAudit.getStaticAgentVersion();
    }


}
