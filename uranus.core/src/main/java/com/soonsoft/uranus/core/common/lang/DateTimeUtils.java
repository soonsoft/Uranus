package com.soonsoft.uranus.core.common.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期时间工具函数集
 */
public abstract class DateTimeUtils {

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DAY_PATTERN = "yyyy-MM-dd";

    public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static Date parseDay(String dateText) {
        return parse(dateText, DAY_PATTERN);
    }

    public static Date parse(String dateText) {
        return parse(dateText, DAY_PATTERN);
    }

    public static Date parse(String dateText, String pattern) {
        if(StringUtils.isEmpty(dateText)) {
            throw new IllegalArgumentException("the arguments dateText is required.");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(dateText);
        } catch (ParseException e) {
            throw new DateTimeException(dateText + " parse to java.util.Date occur error.", e);
        }
    }

    public static String format(Date date) {
        return format(date, DATE_FORMAT, null);
    }

    public static String formatUTC(Date date) {
        return format(date, DATE_FORMAT, "UTC");
    }

    public static String formatWithMillis(Date date) {
        return format(date, DATETIME_MS_PATTERN, null);
    }

    private static String format(Date date, String formatText, String timezoneId) {
        if(date == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(formatText);
        if(timezoneId != null) {
            formatter.setTimeZone(TimeZone.getTimeZone(timezoneId));
        }
        return formatter.format(date);
    }

    public static abstract class ISO8601 {
        public static Date parse(String dateText) {
            if(StringUtils.isEmpty(dateText)) {
                throw new IllegalArgumentException("the arguments dateText is required.");
            }

            SimpleDateFormat formatter = new SimpleDateFormat(ISO8601_FORMAT);
            try {
                return formatter.parse(dateText);
            } catch (ParseException e) {
                throw new DateTimeException(dateText + " parse to java.util.Date by ISO8601 occur error.", e);
            }
        }

        public static String format(Date date) {
            return DateTimeUtils.format(date, ISO8601_FORMAT, null);
        }

        public static String formatUTC(Date date) {
            return DateTimeUtils.format(date, ISO8601_FORMAT, "UTC");
        }
    }
}