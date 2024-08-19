package com.soonsoft.uranus.security.entity;

import java.util.List;

import com.soonsoft.uranus.security.entity.StatusConst.RoleStatus;

public class RoleInfo {

    private String roleCode;

    private String roleName;

    private String description;

    private String roleStatus = RoleStatus.ENABLED;

    private List<String> resourceCodeList;

    public RoleInfo() {

    }

    public RoleInfo(String roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }


    public String getRoleCode() {
        return roleCode;
    }
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleStatus() {
        return roleStatus;
    }
    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    public List<String> getResourceCodeList() {
        return resourceCodeList;
    }

    public void setResourceCodeList(List<String> resourceCodeList) {
        this.resourceCodeList = resourceCodeList;
    }
}