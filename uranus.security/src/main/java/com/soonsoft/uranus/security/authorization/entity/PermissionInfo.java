package com.soonsoft.uranus.security.authorization.entity;

import org.springframework.security.access.ConfigAttribute;

public class PermissionInfo implements ConfigAttribute {

    private final String role;

    public PermissionInfo(String role) {
        this.role = role;
    }

    @Override
    public String getAttribute() {
        return role;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public boolean equals(Object target) {
        if(target == null) {
            return false;
        }

        if(target instanceof PrivilegeInfo p) {
            return p.getAttribute() != null && p.getAttribute().equals(this.role);
        }
        return false;
    }

    @Override
    public String toString() {
        return "[PermissionInfo] " + String.valueOf(role);
    }
    
}
