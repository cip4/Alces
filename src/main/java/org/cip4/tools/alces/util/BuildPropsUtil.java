package org.cip4.tools.alces.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by stefan on 07.12.15.
 */
public class BuildPropsUtil {

    private static Logger log = LoggerFactory.getLogger(BuildPropsUtil.class);

    private static final String RES_BUILD_PROPS = "/org/cip4/tools/alces/build.properties";

    private static Properties props;

    /**
     * If necessary creates and returns the Properties object.
     * @return The current Properties object
     */
    private static Properties getProps() {
        if(props == null) {
            props = new Properties();

            try {
                props.load(BuildPropsUtil.class.getResourceAsStream(RES_BUILD_PROPS));
            } catch (IOException e) {
                log.warn("Error during reading build.properties file.", e);
            }
        }

        return props;
    }

    /**
     * Returns the applications name as String.
     * @return The applications name as String.
     */
    public static String getAppName() {
        return getProps().getProperty("name");
    }

    /**
     * Returns the applications version as String.
     * @return The applications version as String.
     */
    public static String getAppVersion() {
        return getProps().getProperty("version");
    }

    /**
     * Returns the applications build date as String.
     * @return The applications build date as String.
     */
    public static String getBuildDate() {
        return getProps().getProperty("build.date");
    }

}
