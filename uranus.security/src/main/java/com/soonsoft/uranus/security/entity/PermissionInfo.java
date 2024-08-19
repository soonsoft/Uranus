package com.soonsoft.uranus.security.entity;

public class PermissionInfo {

    private final String roleCode;
    private String roleName;
    private final String resourceCode;
    private String resourceName;

    public PermissionInfo(String roleCode, String resourceCode) {
        this.roleCode = roleCode;
        this.resourceCode = resourceCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
