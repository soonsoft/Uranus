package com.soonsoft.uranus.services.membership.dto;

import java.util.List;

/**
 * AuthRole
 */
public class AuthRole {

    public static final Integer ENABLED = 1;

    public static final Integer DISABLED = 0;

    private String roleId;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AuthRole other = (AuthRole) obj;
        if (this.hashCode() != other.hashCode()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AuthRole [description=" + description + ", roleId=" + roleId + ", roleName=" + roleName + ", status=" + status + "]";
    }

    //#region getter and setter

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
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