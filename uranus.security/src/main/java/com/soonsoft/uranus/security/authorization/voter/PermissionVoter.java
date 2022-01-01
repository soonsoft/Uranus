package com.soonsoft.uranus.security.authorization.voter;

import java.util.Collection;
import java.util.stream.Collectors;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.PermissionInfo;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;


public class PermissionVoter extends RoleVoter {

    @Override
    public String getRolePrefix() {
        return "";
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return !StringUtils.isEmpty(attribute.getAttribute());
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        return super.vote(authentication, object, filter(attributes));
    }

    protected Collection<ConfigAttribute> filter(Collection<ConfigAttribute> attributes) {
        return attributes.stream()
            .filter(c -> c instanceof PermissionInfo)
            .collect(Collectors.toList());
    } 
}