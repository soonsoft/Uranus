package com.soonsoft.uranus.web.util;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.soonsoft.uranus.util.lang.DateTimeUtils;
import com.soonsoft.uranus.web.json.HtmlJsonFactory;

/**
 * HtmlUtils
 */
public abstract class HtmlUtils {

    public static String toJSON(Object target) {
        ObjectMapper objectMapper = new ObjectMapper(new HtmlJsonFactory());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtils.ISO8601_FORMAT));

        try {
            return objectMapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("HtilUtils.toJSON process occurred error.", e);
        }
    }
    
}