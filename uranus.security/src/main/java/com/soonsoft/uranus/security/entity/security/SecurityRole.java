package com.soonsoft.uranus.security.entity.security;

import org.springframework.security.core.GrantedAuthority;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.RoleInfo;

public class SecurityRole extends RoleInfo implements GrantedAuthority {

    private static final String ROLE_PREFIX = "";

    public SecurityRole(String role) {
        this(role, null);
    }

    public SecurityRole(String role, String roleName) {
        setRoleCode(role);
        setRoleName(roleName);
    }

    protected String getPrefix() {
        return ROLE_PREFIX;
    }

    @Override
    public void setRoleCode(String role) {
        if(StringUtils.isEmpty(role)) {
            throw new IllegalArgumentException("the argument role can not be null or empty.");
        }
        String prefix = getPrefix();
        if(StringUtils.isEmpty(prefix) || role.startsWith(prefix)) {
            super.setRoleCode(role);
        } else {
            super.setRoleCode(prefix + role);
        }
    }

    //#region GrantedAuthority

    @Override
    public String getAuthority() {
        return getRoleCode();
    }

    //#endregion

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof RoleInfo that) {
            String thisRole = this.getRoleCode();
            String thatRole = that.getRoleCode();
			return thisRole != null && thatRole != null && thisRole.equals(thatRole);
		}

		return false;
	}

	@Override
	public int hashCode() {
        String role = getRoleCode();
		return role == null ? 0 : role.hashCode();
	}

	@Override
	public String toString() {
		return this.getRoleCode();
	}
    
}
