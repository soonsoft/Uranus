package com.soonsoft.uranus.web.mvc.model;

import java.util.LinkedHashMap;
import java.util.UUID;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.error.format.FormatException;
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

    public UUID getUUID(String parameterName) {
        return getUUID(parameterName, null);
    }

    public UUID getUUID(String parameterName, UUID defaultValue) {
        Object value = get(parameterName);

        if(value == null) {
            return defaultValue;
        }

        if(value instanceof UUID) {
            return (UUID) value;
        }
        
        if(value instanceof String) {
            return UUID.fromString((String) value);
        }

        if(value instanceof byte[]) {
            return UUID.nameUUIDFromBytes((byte[]) value);
        }

        throw new FormatException(
            StringUtils.format("the parameter [{0}] value [{0}] can not parse to UUID.", parameterName, String.valueOf(value)));
    }

    public String toJSON() {
        return HtmlUtils.toJSON(this);
    }
    
}