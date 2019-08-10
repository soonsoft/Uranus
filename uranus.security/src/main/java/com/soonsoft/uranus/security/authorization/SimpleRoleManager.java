package com.soonsoft.uranus.security.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.core.GrantedAuthority;

/**
 * SampleRoleManager
 */
public class SimpleRoleManager implements IRoleManager {

    @Override
    public boolean createRole(RoleInfo role) {
        return false;
    }

    @Override
    public boolean updateRole(RoleInfo role) {
        return false;
    }

    @Override
    public boolean deleteRole(String role) {
        return false;
    }

    @Override
    public Collection<GrantedAuthority> getUserRoles(UserInfo user) {
        return null;
    }

    @Override
    public Map<String, List<RoleInfo>> getFunctionRoles(Collection<String> resourceCodes) {
        return null;
    }

    
}