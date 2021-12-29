package com.soonsoft.uranus.services.membership.model;

import com.soonsoft.uranus.security.entity.RoleInfo;

public class MembershipRole extends RoleInfo {

    static final String EMPTY_ROLE = "__EMPTY__ROLE";

    public MembershipRole() {
        this(EMPTY_ROLE);
    }

    public MembershipRole(String role) {
        super(role);
    }

    public MembershipRole(String role, String roleName) {
        super(role, roleName);
    }

    @Override
    protected String getPrefix() {
        return null;
    }
}