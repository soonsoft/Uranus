package com.soonsoft.uranus.services.membership.dao;

import com.soonsoft.uranus.services.membership.dto.AuthPassword;

/**
 * AuthPasswordDAO
 */
public class AuthPasswordDAO extends BaseDAO {

    public AuthPassword getUserPassword(String userId) {
        return this.getMembershipAccess().get("membership.auth_password.getByUserId", userId);
    }

    public int insert(AuthPassword password) {
        return this.getMembershipAccess().insert("membership.auth_password.insert", password);
    }

    public int update(AuthPassword password) {
        return this.getMembershipAccess().update("membership.auth_password.update", password);
    }
}