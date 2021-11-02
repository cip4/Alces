package org.cip4.tools.alces.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

    @Test
    public void millis2readable_1() throws Exception {

        // arrange
        long time = 1596463895922L;

        // act
        String result = TimeUtil.millis2readable(time);

        // assert
        assertEquals("2020-08-03T14:11:35Z", result, "Time string is wrong.");
    }

    @Test
    public void millis2readable_2() throws Exception {

        // arrange
        long time = 1596463895922L;

        // act
        String result = TimeUtil.millis2readable(time, "yyyy-MM-dd");

        // assert
        assertEquals("2020-08-03", result, "Time string is wrong.");
    }

    @Test
    public void duration2readable_1() throws Exception {

        // arrange
        long duration = (23 * 24 * 3600 + 2 * 60 + 1) * 1000 + 12; // 23d 0h 2m 1s

        // act
        String result = TimeUtil.duration2readable(duration);

        // assert
        assertEquals("23d 00h 02m 01s", result, "Duration is wrong.");
    }

    @Test
    public void duration2readable_2() throws Exception {

        // arrange
        long duration = (2 * 24 * 3600 + 22 * 3600 + 42 * 60 + 31) * 1000 + 361; // 2d 22h 42m 31s

        // act
        String result = TimeUtil.duration2readable(duration);

        // assert
        assertEquals("2d 22h 42m 31s", result, "Duration is wrong.");
    }
}