package com.soonsoft.uranus.api.model;

import com.soonsoft.uranus.web.mvc.model.JsonResult;

public class APIResult extends JsonResult {

    private final static String STATUS_CODE_NAME = "statusCode";
    
    protected APIResult() {
        super();
        setStatusCode(0);
        setSuccess(true);
    }

    /**
     * 设置错误码
     * @param code 错误码（成功： 0， Http错误：三位HttpStatus，业务错误：四位及以上）
     */
    public void setStatusCode(int code) {
        put(STATUS_CODE_NAME, code);
        if(code != 0) {
            setSuccess(false);
        }
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

    public static APIResult create(Object data, int total) {
        APIResult result = new APIResult();
        result.setData(data);
        result.setTotalRows(total);
        return result;
    }

}
