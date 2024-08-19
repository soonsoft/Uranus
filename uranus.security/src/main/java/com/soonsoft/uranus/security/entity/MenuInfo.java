package com.soonsoft.uranus.security.entity;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceType;

/**
 * 菜单信息
 */
public class MenuInfo extends FunctionInfo {

    private String icon;

    public MenuInfo(String resourceCode, String menuName, String url) {
        super(resourceCode, menuName, url);
        setType(ResourceType.MENU);
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

    public boolean isFolder() {
        return StringUtils.isEmpty(getUrl());
    }

}