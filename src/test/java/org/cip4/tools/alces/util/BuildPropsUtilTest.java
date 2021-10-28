package org.cip4.tools.alces.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by stefan on 07.12.15.
 */
public class BuildPropsUtilTest {

    @Test
    public void testGetAppName() throws Exception {
        final String appName = BuildPropsUtil.getAppName();
        Assertions.assertEquals("AppName is wrong.", "TEST_NAME", appName);
    }

    @Test
    public void testGetAppVersion() throws Exception {
        final String appVersion = BuildPropsUtil.getAppVersion();
        Assertions.assertEquals("AppVersion is wrong.", "TEST_VERSION", appVersion);
    }

    @Test
    public void testGetBuildDate() throws Exception {
        final String buildDate = BuildPropsUtil.getBuildDate();
        Assertions.assertEquals("BuildDate is wrong.", "TEST_DATE", buildDate);
    }
}