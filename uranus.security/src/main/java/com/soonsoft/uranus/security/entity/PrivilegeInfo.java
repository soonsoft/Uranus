package com.soonsoft.uranus.security.entity;

public class PrivilegeInfo {

    private final String userId;
    private String userName;
    private final String resourceCode;
    private String resourceName;

    public PrivilegeInfo(String userId, String resourceCode) {
        this.userId = userId;
        this.resourceCode = resourceCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String username) {
        this.userName = username;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }    
    
}
