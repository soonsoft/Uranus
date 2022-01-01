package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "auth_user_role_relation")
public class AuthUserRoleRelation {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    private String username;

    @Id
    @Column(name = "role_id")
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