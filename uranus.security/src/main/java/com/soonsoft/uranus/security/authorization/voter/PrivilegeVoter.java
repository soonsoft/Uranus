package com.soonsoft.uranus.security.authorization.voter;

import java.util.Collection;
import java.util.stream.Collectors;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authorization.entity.PrivilegeInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public class PrivilegeVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return !StringUtils.isEmpty(attribute.getAttribute());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
			return ACCESS_DENIED;
		}
		int result = ACCESS_ABSTAIN;

        Object principal = authentication.getPrincipal();
        String userId = principal instanceof UserInfo ? ((UserInfo) principal).getUserId() : (String) principal;

		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				result = ACCESS_DENIED;
                if(attribute.getAttribute().equals(userId)) {
                    return ACCESS_GRANTED;
                }
			}
		}
		return result;
    }

    protected Collection<ConfigAttribute> filter(Collection<ConfigAttribute> attributes) {
        return attributes.stream()
            .filter(c -> c instanceof PrivilegeInfo)
            .collect(Collectors.toList());
    }
    
}
