package com.soonsoft.uranus.web.error.vo;

public class JsonErrorModel {
    private int statusCode;
    private String message;

    public JsonErrorModel() {

    }

    public JsonErrorModel(WebErrorModel webErrorModel) {
        this.statusCode = webErrorModel.getStatusCode();
        this.message = webErrorModel.getMessage();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
