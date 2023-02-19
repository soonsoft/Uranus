package com.soonsoft.uranus.core.common.beans;

public class TestBean {
    private Integer intValue;
    private Boolean boolValue;
    private String strValue;
    private String[] arrValue;

    public Integer getIntValue() {
        return intValue;
    }
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
    // public Boolean isBoolValue() {
    //     return boolValue;
    // }
    // public void setBoolValue(Boolean boolValue) {
    //     this.boolValue = boolValue;
    // }
    
    public Boolean getBoolValue() {
        return boolValue;
    }
    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public String getStrValue() {
        return strValue;
    }
    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public String[] getArrValue() {
        return arrValue;
    }
    public void setArrValue(String[] arrValue) {
        this.arrValue = arrValue;
    }

    public String getName() {
        return "周星星";
    }

    
}
