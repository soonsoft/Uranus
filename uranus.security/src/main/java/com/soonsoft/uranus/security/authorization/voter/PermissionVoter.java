package com.soonsoft.uranus.security.authorization.voter;

import java.util.Collection;
import java.util.stream.Collectors;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.PermissionInfo;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class PermissionVoter implements IVoter {

    public boolean supports(ConfigAttribute attribute) {
        return !StringUtils.isEmpty(attribute.getAttribute());
    }

    @Override
    public boolean vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Collection<ConfigAttribute> permissionList = filter(attributes);
		for (ConfigAttribute attribute : permissionList) {
			if (this.supports(attribute)) {
				for (GrantedAuthority authority : authorities) {
					if (attribute.getAttribute().equals(authority.getAuthority())) {
						return true;
					}
				}
			}
		}
		return false;
    }

    protected Collection<ConfigAttribute> filter(Collection<ConfigAttribute> attributes) {
        return attributes.stream()
            .filter(c -> c instanceof PermissionInfo)
            .collect(Collectors.toList());
    } 
}