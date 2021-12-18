package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.services.membership.po.AuthUser;


public class AuthUserDAO extends BaseDAO {

    public AuthUser getUser(String userName) {
        return getMembershipAccess().get("membership.auth_user.getByUserName", userName);
    }

    public int insert(AuthUser user) {
        return getMembershipAccess().insert("membership.auth_user.insert", user);
    }

    public int update(AuthUser user) {
        return getMembershipAccess().update("membership.auth_user.update", user);
    }

    public int delete(UUID userId) {
        return getMembershipAccess().delete("membership.auth_user.delete", userId);
    }

    public int deleteByUserName(String username) {
        return getMembershipAccess().delete("membership.auth_user.deleteByUserName", username);
    }

    public List<AuthUser> select(Map<String, Object> params, Page page) {
        return getMembershipAccess().select("membership.auth_user.select", params, page);
    }
    
}