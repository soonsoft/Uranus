package com.soonsoft.uranus.web.mvc.model;

import java.util.LinkedHashMap;

import com.soonsoft.uranus.web.util.HtmlUtils;

public class RequestData extends LinkedHashMap<String, Object> implements IRequestData {

    private static final long serialVersionUID = 1L;

    public RequestData() {
        super();
    }

    public RequestData(int capacity) {
        super((int) ((float)capacity / 0.75f + 1.0f));
    }

    @Override
    public String get(String parameterName, String defaultValue) {
        Object value = super.get(parameterName);
        String str;
        if(value instanceof String) {
            str = (String) value;
        } else {
            str = value == null ? null : value.toString();
        }
        return value != null ? str : defaultValue;
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T getObject(String parameterName) {
        return (T) super.get(parameterName);
    }

    public String toJSON() {
        return HtmlUtils.toJSON(this);
    }
    
}