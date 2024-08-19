package com.soonsoft.uranus.security.simple.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.error.UnsupportedException;
import com.soonsoft.uranus.core.model.data.IPagingList;
import com.soonsoft.uranus.core.model.data.PagingList;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.core.GrantedAuthority;

public class SimpleRoleManager implements IRoleManager {

    private List<RoleInfo> roleInfos;

    public SimpleRoleManager() {

    }

    public void setRoleInfos(List<RoleInfo> roleInfos) {
        this.roleInfos = roleInfos;
    }

    public List<RoleInfo> getRoleInfos() {
        return roleInfos;
    }

    //#region IRoleManager Unsupported Methods

    @Override
    public IPagingList<RoleInfo> queryRoles(Map<String, Object> params, int pageIndex, int pageSize) {
        List<RoleInfo> result = getRoleInfos();
        PagingList<RoleInfo> pageList = new PagingList<>(result);
        pageList.setPageIndex(pageIndex);
        pageList.setPageSize(pageSize);
        return pageList;
    }

    @Override
    public boolean createRole(RoleInfo role) {
        return roleInfos.add(role);
    }

    @Override
    public boolean updateRole(RoleInfo role) {
        for(RoleInfo roleInfo : roleInfos) {
            if(roleInfo.getRoleCode().equals(role.getRoleCode())) {
                roleInfo.setRoleName(role.getRoleName());
                roleInfo.setRoleStatus(role.getRoleStatus());
                roleInfo.setDescription(role.getDescription());
                roleInfo.setResourceCodeList(role.getResourceCodeList());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteRole(String role) {
        int index = -1;
        boolean flag = false;
        for (RoleInfo roleInfo : roleInfos) {
            if(roleInfo != null && roleInfo.getRoleCode().equals(role)) {
                flag = true;
                break;
            }
            index++;
        }
        if(flag) {
            roleInfos.remove(index);
        }
        return flag;
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