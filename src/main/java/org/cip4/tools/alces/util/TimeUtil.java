package org.cip4.tools.alces.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Util class all around time units.
 */
public class TimeUtil {

    /**
     * Helper method for converting milli seconds to a human readable form.
     *
     * @param millis The millis to be converted.
     * @return Timestamp as human readable text.
     */
    public static String millis2readable(long millis) {
        return millis2readable(millis, "yyyy-MM-dd'T'HH:mm:ssXXX");
    }

    /**
     * Helper method for converting milli seconds to a human readable form.
     *
     * @param millis The millis to be converted.
     * @param format The target readable time format.
     * @return Timestamp as human readable text.
     */
    public static String millis2readable(long millis, String format) {
        SimpleDateFormat dateFormat = getDateFormat(format, ZoneId.of("GMT"));
        return dateFormat.format(new Date(millis));
    }

    /**
     * Helper method for converting a duration in a human readable string.
     * @param millis The duration as milliseconds.
     * @return The duration as human readable string.
     */
    public static String duration2readable(long millis) {
        long seconds = Math.abs(millis / 1000);

        return String.format(
                "%dd %02dh %02dm %02ds",
                seconds / (3600 * 24),
                (seconds % (3600 * 24)) / 3600,
                (seconds % 3600) / 60,
                seconds % 60);
    }

    /**
     * Returns a simple date format object.
     *
     * @param format   The date format object.
     * @param zoneId The time zone identifier.
     */
    private static SimpleDateFormat getDateFormat(String format, ZoneId zoneId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        if (zoneId != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(zoneId));
        }

        return dateFormat;
    }
}
