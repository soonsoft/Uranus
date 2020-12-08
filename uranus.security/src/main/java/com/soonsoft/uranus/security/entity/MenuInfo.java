package com.soonsoft.uranus.security.entity;

/**
 * 菜单信息
 */
public class MenuInfo extends FunctionInfo {

    private String icon;

    public MenuInfo(String resourceCode, String menuName, String url) {
        super(resourceCode, menuName, url);
        setType(MENU_TYPE);
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

}