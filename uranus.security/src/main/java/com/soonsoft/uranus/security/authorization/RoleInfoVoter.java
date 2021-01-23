package com.soonsoft.uranus.security.authorization;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;

/**
 * MembershipVoter
 */
public class RoleInfoVoter extends RoleVoter {

    @Override
    public String getRolePrefix() {
        return "";
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        if (!StringUtils.isEmpty(attribute.getAttribute())) {
			return true;
		} else {
			return false;
		}
    }
}