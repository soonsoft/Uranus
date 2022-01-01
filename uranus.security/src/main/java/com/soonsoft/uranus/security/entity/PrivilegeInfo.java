package com.soonsoft.uranus.security.entity;

import org.springframework.security.access.ConfigAttribute;

public class PrivilegeInfo implements ConfigAttribute {

    private final String privilegeCode;

    public PrivilegeInfo(String code) {
        this.privilegeCode = code;
    }

    @Override
    public String getAttribute() {
        return privilegeCode;
    }

    @Override
    public int hashCode() {
        return this.privilegeCode.hashCode();
    }

    @Override
    public boolean equals(Object target) {
        if(target == null) {
            return false;
        }

        if(target instanceof PrivilegeInfo p) {
            return p.getAttribute() != null && p.getAttribute().equals(this.privilegeCode);
        }
        return false;
    }

    @Override
    public String toString() {
        return "[PrivilegeInfo] " + String.valueOf(privilegeCode);
    }
    
}
