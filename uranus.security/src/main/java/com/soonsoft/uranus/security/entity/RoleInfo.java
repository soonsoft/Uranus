package com.soonsoft.uranus.security.entity;

import com.soonsoft.uranus.util.lang.StringUtils;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;

/**
 * RoleInfo
 */
public class RoleInfo implements GrantedAuthority, ConfigAttribute {

    private static final long serialVersionUID = -8108066658263431548L;

    private static final String ROLE_PREFIX = "ROLE_";

    private String role;

    private String roleName;

    private String description;

    private boolean enable;

    public RoleInfo(String role) {
        this(role, null);
    }

    public RoleInfo(String role, String roleName) {
        setRole(role);
        setRoleName(roleName);
    }

    protected String getPrefix() {
        return ROLE_PREFIX;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    private void setRole(String role) {
        if(StringUtils.isEmpty(role)) {
            throw new IllegalArgumentException("the argument role can not be null or empty.");
        }
        String prefix = getPrefix();
        if(StringUtils.isEmpty(prefix) || role.startsWith(prefix)) {
            this.role = role;
        } else {
            this.role = prefix + role;
        }
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @param enable the enable to set
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getAuthority() {
        return getRole();
    }

    @Override
    public String getAttribute() {
        return getRole();
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof RoleInfo) {
			return role.equals(((RoleInfo) obj).role);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.role.hashCode();
	}

	@Override
	public String toString() {
		return this.role;
	}
}