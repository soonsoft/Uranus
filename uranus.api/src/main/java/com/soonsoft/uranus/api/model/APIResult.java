package com.soonsoft.uranus.api.model;

import com.soonsoft.uranus.web.mvc.model.JsonResult;

public class APIResult extends JsonResult {

    private final static String STATUS_CODE_NAME = "statusCode";
    
    protected APIResult() {
        super();
        setStatusCode(0);
        
    }

    public void setStatusCode(int code) {
        put(STATUS_CODE_NAME, code);
    }

    public int getStatusCode() {
        Object code = get(STATUS_CODE_NAME);
        return code instanceof Integer ? ((Integer)code).intValue() : 0;
    }

    public static APIResult create(int statusCode, String message) {
        APIResult result = new APIResult();
        result.setStatusCode(statusCode);
        result.setMessage(message);
        return result;
    }

    public static APIResult create(Object data) {
        APIResult result = new APIResult();
        result.setData(data);
        return result;
    }

}
