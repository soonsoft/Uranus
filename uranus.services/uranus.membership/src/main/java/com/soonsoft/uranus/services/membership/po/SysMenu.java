package com.soonsoft.uranus.services.membership.po;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * SysMenu
 */
public class SysMenu {

    public static final Integer STATUS_ENABLED = 1;

    public static final Integer STATUS_DISABLED = 0;

    public static final String TYPE_MENU = "menu";

    public static final String TYPE_ACTION = "action";

    private UUID functionId;

    private String functionName;

    private UUID parentId;

    private String description;

    /**
     * 功能类型 menu: 菜单, action: 操作
     */
    private String type = TYPE_MENU;

    /**
     * 状态 1: 有效, 2: 无效
     */
    private Integer status = STATUS_ENABLED;

    /**
     * 排序
     */
    private int sortValue = 0;

    private String menuKey;

    private String url;

    private String icon;

    private String background;

    private String themeInfo;

    private String tileStyle;

    private Collection<AuthRole> roles;

    public UUID getFunctionId() {
        return functionId;
    }

    public void setFunctionId(UUID functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public int getSortValue() {
        return sortValue;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getThemeInfo() {
        return themeInfo;
    }

    public void setThemeInfo(String themeInfo) {
        this.themeInfo = themeInfo;
    }

    public String getTileStyle() {
        return tileStyle;
    }

    public void setTileStyle(String tileStyle) {
        this.tileStyle = tileStyle;
    }

    public Collection<AuthRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<AuthRole> roles) {
        this.roles = roles;
    }

    public void addRole(AuthRole role) {
        if(role != null) {
            if(this.roles == null) {
                this.roles = new HashSet<>();
            }
            this.roles.add(role);
        }
    }
    
}