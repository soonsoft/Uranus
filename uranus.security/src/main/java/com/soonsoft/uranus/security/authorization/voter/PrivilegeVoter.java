package com.soonsoft.uranus.security.authorization.voter;

import java.util.Collection;
import java.util.stream.Collectors;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.PrivilegeInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public class PrivilegeVoter implements IVoter {

    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof PrivilegeInfo && !StringUtils.isEmpty(attribute.getAttribute());
    }

    @Override
    public boolean vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
			return false;
		}

        Object principal = authentication.getPrincipal();
        String userId = principal instanceof UserInfo ? ((UserInfo) principal).getUserId() : (String) principal;
        Collection<ConfigAttribute> privilegeList = filter(attributes);

		for (ConfigAttribute attribute : privilegeList) {
			if (this.supports(attribute)) {
                if(attribute.getAttribute().equals(userId)) {
                    return true;
                }
			}
		}
		return false;
    }

    protected Collection<ConfigAttribute> filter(Collection<ConfigAttribute> attributes) {
        return attributes.stream()
            .filter(c -> c instanceof PrivilegeInfo)
            .collect(Collectors.toList());
    }
    
}
