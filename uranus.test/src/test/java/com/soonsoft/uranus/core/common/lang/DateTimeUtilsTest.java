package com.soonsoft.uranus.core.common.lang;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeUtilsTest {

    @Test
    public void test_format() {
        String dateText = "2019-05-18 20:30:00";
        Date date = DateTimeUtils.parse(dateText);
        Assert.assertNotNull(date);
        Assert.assertTrue(dateText.equals(DateTimeUtils.format(date)));

        String iso8601Text = DateTimeUtils.ISO8601.format(date);
        Assert.assertTrue("2019-05-18T20:30:00.000+08:00".equals(iso8601Text));
        String utc = DateTimeUtils.ISO8601.formatUTC(date);
        Assert.assertTrue("2019-05-18T12:30:00.000Z".equals(utc));
        Assert.assertNotNull(DateTimeUtils.ISO8601.parse(iso8601Text));
        Assert.assertNotNull(DateTimeUtils.ISO8601.parse("2019-05-18T12:30:00.000Z"));
    }

}