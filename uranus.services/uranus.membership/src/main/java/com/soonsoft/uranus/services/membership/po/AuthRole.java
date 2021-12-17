package com.soonsoft.uranus.services.membership.po;

import java.util.List;
import java.util.UUID;

/**
 * AuthRole
 */
public class AuthRole {

    public static final Integer ENABLED = 1;

    public static final Integer DISABLED = 0;

    private UUID roleId;

    private String roleName;

    private String description;

    /**
     * 状态 1: 有效, 2: 无效
     */
    private Integer status = ENABLED;

    /**
     * 关联的菜单信息
     */
    private List<Object> menus;

    @Override
    public String toString() {
        return "AuthRole [description=" + description 
        + ", roleId=" + roleId != null ? roleId.toString() : null 
        + ", roleName=" + roleName 
        + ", status=" + status + "]";
    }

    //#region getter and setter

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Object> getMenus() {
        return menus;
    }

    public void setMenus(List<Object> menus) {
        this.menus = menus;
    }

    //#endregion
}