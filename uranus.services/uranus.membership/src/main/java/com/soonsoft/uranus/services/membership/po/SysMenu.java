package com.soonsoft.uranus.services.membership.po;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "sys_menu")
public class SysMenu extends SysFunction {

    @Column(name = "menu_key")
    private String menuKey;

    @Column(name = "icon")
    private String icon;

    @Column(name = "background")
    private String background;

    @Column(name = "theme_info")
    private String themeInfo;

    @Column(name = "tile_style")
    private String tileStyle;

    private Collection<AuthRole> roles;

    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
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