package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.services.membership.dto.AuthRole;

/**
 * AuthRoleDAO
 */
public class AuthRoleDAO extends BaseDAO {

    public int insert(AuthRole role) {
        return getMembershipAccess().insert("membership.auth_role.insert", role);
    }

    public int update(AuthRole role) {
        return getMembershipAccess().update("membership.auth_role.update", role);
    }

    public int delete(String roleId) {
        return getMembershipAccess().delete("membership.auth_role.delete", roleId);
    }

    public int deleteByRoleName(String roleName) {
        return getMembershipAccess().delete("membership.auth_role.deleteByRoleName", roleName);
    }

    public List<AuthRole> select(Map<String, Object> params, Page page) {
        return getMembershipAccess().select("membership.auth_role.select", params, page);
    }

}