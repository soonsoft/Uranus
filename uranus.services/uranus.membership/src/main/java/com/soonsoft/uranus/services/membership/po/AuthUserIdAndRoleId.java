package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;


public class AuthUserIdAndRoleId {

    private UUID userId;

    private String username;

    private UUID roleId;

    private String roleName;

    //#region getter and setter

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    //#endregion
}