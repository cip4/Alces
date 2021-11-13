package org.cip4.tools.alces.controller;

import org.cip4.tools.alces.service.about.AboutService;
import org.cip4.tools.alces.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private AboutService aboutService;

    @RequestMapping(value = "/version", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Version version() {
        return new Version();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Status status() {
        return new Status();
    }

    /**
     * Private model class for status.
     */
    private class Status {
        private final String status;
        private final long startTime;
        private final String startTimeReadable;
        private final long uptime;
        private final String uptimeReadable;
        private final String hostname;
        private final Version version;

        private Status() {
            this.status = "UP";
            this.version = new Version();
            this.startTime = aboutService.getStartTime();
            this.startTimeReadable = TimeUtil.millis2readable(aboutService.getStartTime());
            this.uptime = System.currentTimeMillis() - aboutService.getStartTime();
            this.uptimeReadable = TimeUtil.duration2readable(this.uptime);
            this.hostname = aboutService.getHostname();
        }

        public String getStatus() {
            return status;
        }

        public long getStartTime() {
            return startTime;
        }

        public String getStartTimeReadable() {
            return startTimeReadable;
        }

        public long getUptime() {
            return uptime;
        }

        public String getUptimeReadable() {
            return uptimeReadable;
        }

        public String getHostname() {
            return hostname;
        }

        public Version getVersion() {
            return version;
        }

    }

    /**
     * Private model class for version
     */
    private class Version {

        private final String appName;
        private final String appVersion;
        private final String buildTime;
        private final String jdfLibJVersion;

        private Version() {
            this.appName = aboutService.getAppName();
            this.appVersion = aboutService.getAppVersion();
            this.buildTime = aboutService.getBuildTime();
            this.jdfLibJVersion = aboutService.getJdfLibJVersion();
        }

        public String getAppName() {
            return appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public String getJdfLibJVersion() {
            return jdfLibJVersion;
        }
    }
}
