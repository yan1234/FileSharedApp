package com.example.filesharedapp.utils.common;

import static org.junit.Assert.*;

/**
 * Created by 272388 on 2016/11/9.
 */
public class DateUtilsTest {

    private DateUtils dateUtils;

    @org.junit.Before
    public void setUp() throws Exception {
        dateUtils = new DateUtils();
    }

    @org.junit.Test
    public void testMillisToTime() throws Exception {
        long[] s = new long[7];
        s[0] = 0;
        s[1] = 0;
        s[2] = 0;
        s[3] = 0;
        s[4] = 0;
        s[5] = 1;
        s[6] = 500;

        assertArrayEquals(s, DateUtils.millisToTime(1500));
    }
}