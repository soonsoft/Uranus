package com.soonsoft.uranus.services.membership.model;

import com.soonsoft.uranus.security.entity.PrivilegeInfo;

public class MembershipPrivilegeInfo extends PrivilegeInfo {

    private final String privilegeName;

    public MembershipPrivilegeInfo(String userId, String code, String name) {
        super(userId, code);
        this.privilegeName = name;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }
    
}
