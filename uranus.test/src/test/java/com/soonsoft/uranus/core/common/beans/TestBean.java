package com.soonsoft.uranus.core.common.beans;

public class TestBean {
    private int intValue;
    private boolean boolValue;
    private String strValue;
    private String[] arrValue;

    public int getIntValue() {
        return intValue;
    }
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    public boolean isBoolValue() {
        return boolValue;
    }
    public void setBoolValue(boolean boolValue) {
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
        return "my Name";
    }
}
