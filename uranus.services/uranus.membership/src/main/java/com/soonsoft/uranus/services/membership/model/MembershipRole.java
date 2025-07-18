package com.soonsoft.uranus.services.membership.model;

import com.soonsoft.uranus.security.entity.security.SecurityRole;

public class MembershipRole extends SecurityRole {

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