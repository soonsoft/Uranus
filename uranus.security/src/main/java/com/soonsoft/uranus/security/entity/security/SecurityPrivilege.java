package com.soonsoft.uranus.security.entity.security;

import org.springframework.security.access.ConfigAttribute;

import com.soonsoft.uranus.security.entity.PrivilegeInfo;

public class SecurityPrivilege extends PrivilegeInfo implements ConfigAttribute {

    public SecurityPrivilege(String userId, String resourceCode) {
        super(userId, resourceCode);
    }

    @Override
    public String getAttribute() {
        return this.getUserId();
    }
    
    @Override
    public int hashCode() {
        return this.getUserId() == null ? 0 : this.getUserId().hashCode();
    }

    @Override
    public boolean equals(Object target) {
        if(target == null) {
            return false;
        }

        if(target instanceof PrivilegeInfo p) {
            return p.getUserId() != null && p.getUserId().equals(this.getUserId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "[SecurityPrivilege] " + String.valueOf(getUserId());
    }
    
}
