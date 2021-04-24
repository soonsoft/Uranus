package com.soonsoft.uranus.site.viewmodel;

import java.util.HashMap;

public class WebErrorModel extends HashMap<String, Object> {

    public Integer getStatusCode() {
        return (Integer) get("statusCode");
    }

    public void setStatusCode(int statusCode) {
        put("statusCode", Integer.valueOf(statusCode));
    }

    public String getStatusName() {
        return (String) get("statusName");
    }

    public void setStatusName(String statusName) {
        put("statusName", statusName);
    }

    public String getMessage() {
        return (String) get("message");
    }

    public void setMessage(String message) {
        put("message", message);
    }

    public Throwable getException() {
        return (Throwable) get("exception");
    }

    public void setException(Throwable exception) {
        put("exception", exception);
    }

    @Override
    public Object put(String key, Object value) {
        if(value != null) {
            return super.put(key, value);
        }
        return value;
    }

}
