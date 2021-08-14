package com.soonsoft.uranus.security.authorization;

import com.soonsoft.uranus.core.common.lang.StringUtils;

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