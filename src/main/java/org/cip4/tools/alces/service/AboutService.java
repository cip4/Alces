package org.cip4.tools.alces.service;

/**
 * Business interface of the about service.
 */
public interface AboutService {

    /**
     * Returns the applications name as String.
     * @return The applications name as String.
     */
    String getAppName();

    /**
     * Returns the applications version as String.
     * @return The applications version as String.
     */
    String getAppVersion();

    /**
     * Returns the applications build time as String.
     * @return The applications build time as String.
     */
    String getBuildTime();

    /**
     * Returns the service's start time.
     * @return The service's start time.
     */
    long getStartTime();

    /**
     * Returns the service's hostname.
     * @return The service's hostname.
     */
    String getHostname();

}
