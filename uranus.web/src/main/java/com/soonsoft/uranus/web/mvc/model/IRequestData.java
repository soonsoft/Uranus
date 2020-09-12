package com.soonsoft.uranus.web.mvc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.soonsoft.uranus.core.common.lang.DateTimeUtils;

/**
 * IRequestData
 */
public interface IRequestData {

    default String get(String parameterName) {
        return get(parameterName, null);
    }

    String get(String parameterName, String defaultValue);

    default Integer getInteger(String parameterName) {
        return getInteger(parameterName, null);
    }

    default Integer getInteger(String parameterName, Integer defaultValue) {
        String value = get(parameterName);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    default Long getLong(String parameterName) {
        return getLong(parameterName, null);
    }

    default Long getLong(String parameterName, Integer defaultValue) {
        String value = get(parameterName);
        return value != null ? Long.valueOf(value) : defaultValue;
    }
    
    default Float getFloat(String parameterName) {
        return getFloat(parameterName, null);
    }

    default Float getFloat(String parameterName, Float defaultValue) {
        String value = get(parameterName);
        return value != null ? Float.valueOf(value) : defaultValue;
    }

    default Double getDouble(String parameterName) {
        return getDouble(parameterName, null);
    }

    default Double getDouble(String parameterName, Double defaultValue) {
        String value = get(parameterName);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

    default Boolean getBoolean(String parameterName) {
        return getBoolean(parameterName, null);
    }

    default Boolean getBoolean(String parameterName, Boolean defaultValue) {
        String value = get(parameterName);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }

    default BigDecimal getBigDecimal(String parameterName) {
        return getBigDecimal(parameterName, null);
    }

    default BigDecimal getBigDecimal(String parameterName, BigDecimal defaultValue) {
        String value = get(parameterName);
        return value != null ? new BigDecimal(value) : defaultValue;
    }

    default Date getDate(String parameterName) {
        return getDate(parameterName, null);
    }

    default Date getDate(String parameterName, Date defaultValue) {
        String value = get(parameterName);
        return value != null ? DateTimeUtils.parse(value) : defaultValue;
    }

    default Date getJsonDate(String parameterName) {
        return getJsonDate(parameterName, null);
    }

    default Date getJsonDate(String parameterName, Date defaultValue) {
        String value = get(parameterName);
        return value != null ? DateTimeUtils.ISO8601.parse(value) : defaultValue;
    }

}