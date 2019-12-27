package com.soonsoft.uranus.core.common.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTimeUtils
 */
public abstract class DateTimeUtils {

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date parse(String dateText) {
        if(StringUtils.isEmpty(dateText)) {
            throw new IllegalArgumentException("the arguments dateText is required.");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
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

    private static String format(Date date, String formatText, String timezoneId) {
        if(date == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(formatText);
        if(timezoneId != null) {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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