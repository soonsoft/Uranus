package com.soonsoft.uranus.web.mvc.model;

import java.util.HashMap;

/**
 * JsonResult
 */
public class JsonResult extends HashMap<String, Object> implements IResultData {

    private static final long serialVersionUID = -917374424653417072L;

    private final static String SUCCESS_NAME = "success";

    private final static String TOTAL_NAME = "total";

    private final static String MESSAGE_NAME = "message";

    private final static String DATA_NAME = "data";

    private JsonResult() {
        put(SUCCESS_NAME, Boolean.TRUE);
    }
    

    @Override
    public IResultData setSuccess(boolean success) {
        put(SUCCESS_NAME, success);
        return this;
    }

    @Override
    public IResultData setTotalRows(int total) {
        put(TOTAL_NAME, total);
        return this;
    }

    @Override
    public IResultData setData(Object data) {
        put(DATA_NAME, data);
        return this;
    }

    @Override
    public IResultData setMessage(String message) {
        put(MESSAGE_NAME, message);
        return this;
    }

    @Override
    public IResultData setValue(String name, Object value) {
        put(name, value);
        return this;
    }

    public static IResultData create() {
        return new JsonResult();
    }

    public static IResultData create(String message) {
        JsonResult result = new JsonResult();
        result.setMessage(message);
        return result;
    }

    public static IResultData create(Object value, int total) {
        JsonResult result = new JsonResult();
        result.setData(value);
        result.setTotalRows(total);
        return result;
    }

    public static IResultData getTrue() {
        return new JsonResult().setSuccess(Boolean.TRUE);
    }
    
    
}