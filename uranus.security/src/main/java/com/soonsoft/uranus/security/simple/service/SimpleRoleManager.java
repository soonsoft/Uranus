package com.soonsoft.uranus.security.simple.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.error.UnsupportedException;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.core.GrantedAuthority;

public class SimpleRoleManager implements IRoleManager {

    private List<? extends RoleInfo> roleInfos;

    public SimpleRoleManager() {

    }

    public void setRoleInfos(List<? extends RoleInfo> roleInfos) {
        this.roleInfos = roleInfos;
    }

    public List<? extends RoleInfo> getRoleInfos() {
        return roleInfos;
    }

    //#region IRoleManager Unsupported Methods

    @Override
    public boolean createRole(RoleInfo role) {
        throw new UnsupportedException();
    }

    @Override
    public boolean updateRole(RoleInfo role) {
        throw new UnsupportedException();
    }

    @Override
    public boolean deleteRole(String role) {
        throw new UnsupportedException();
    }

    @Override
    public Collection<GrantedAuthority> getUserRoles(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public Map<String, List<RoleInfo>> getFunctionRoles(Collection<String> resourceCodes) {
        throw new UnsupportedException();
    }

    //#endregion

}