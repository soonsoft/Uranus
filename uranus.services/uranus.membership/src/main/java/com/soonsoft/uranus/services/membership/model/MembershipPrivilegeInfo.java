package com.soonsoft.uranus.services.membership.model;

import com.soonsoft.uranus.security.entity.PrivilegeInfo;

public class MembershipPrivilegeInfo extends PrivilegeInfo {

    private final String privilegeName;

    public MembershipPrivilegeInfo(String code, String name) {
        super(code);
        this.privilegeName = name;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }
    
}
