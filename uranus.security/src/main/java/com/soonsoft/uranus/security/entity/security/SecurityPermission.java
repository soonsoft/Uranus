package com.soonsoft.uranus.security.entity.security;

import org.springframework.security.access.ConfigAttribute;

import com.soonsoft.uranus.security.entity.PermissionInfo;

public class SecurityPermission extends PermissionInfo implements ConfigAttribute {

    public SecurityPermission(String roleCode, String resourceCode) {
        super(roleCode, resourceCode);
    }

    @Override
    public String getAttribute() {
        return this.getRoleCode();
    }

    @Override
    public int hashCode() {
        return this.getRoleCode() == null ? 0 : this.getRoleCode().hashCode();
    }

    @Override
    public boolean equals(Object target) {
        if(target == null) {
            return false;
        }

        if(target instanceof PermissionInfo p) {
            return p.getRoleCode() != null && p.getRoleCode().equals(this.getRoleCode());
        }
        return false;
    }

    @Override
    public String toString() {
        return "[SecurityPermission] " + String.valueOf(getRoleCode());
    }

    
    
}
