package com.soonsoft.uranus.security.entity;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.security.authorization.Resource;

import org.apache.commons.lang3.StringUtils;

/**
 * MenuInfo
 */
public class MenuInfo implements Resource {

    private String resourceCode;

    private String parentResourceCode;

    private String name;

    private String icon;

    private String url;

    private boolean enabled;

    private List<RoleInfo> allowRoles;

    public MenuInfo(String resourceCode, String menuName) {
        if(StringUtils.isEmpty(resourceCode)) {
            throw new IllegalArgumentException("the resourceCode is null or empty.");
        }
        this.resourceCode = resourceCode;
        this.name = menuName;
    }

    @Override
    public String getResourceCode() {
        return resourceCode;
    }

    @Override
    public String getResourceUrl() {
        return getUrl();
    }

    public String getParentResourceCode() {
        return parentResourceCode;
    }

    public void setParentResourceCode(String parentResourceCode) {
        this.parentResourceCode = parentResourceCode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }    

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the allowRoles
     */
    public List<RoleInfo> getAllowRoles() {
        return allowRoles;
    }

    /**
     * @param allowRoles the allowRoles to set
     */
    public void setAllowRoles(List<RoleInfo> allowRoles) {
        this.allowRoles = allowRoles;
    }

    public void addAllowRole(RoleInfo role) {
        if(this.allowRoles == null) {
            this.allowRoles = new ArrayList<>();
        }
        this.allowRoles.add(role);
    }

    @Override
    public int hashCode() {
        return resourceCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
			return true;
		}

		if (obj instanceof MenuInfo) {
			return resourceCode.equals(((MenuInfo) obj).resourceCode);
		}

		return false;
    }

}