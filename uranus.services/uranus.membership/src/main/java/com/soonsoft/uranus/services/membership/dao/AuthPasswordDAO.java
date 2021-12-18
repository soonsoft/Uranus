package com.soonsoft.uranus.services.membership.dao;

import java.util.UUID;

import com.soonsoft.uranus.services.membership.po.AuthPassword;

/**
 * AuthPasswordDAO
 */
public class AuthPasswordDAO extends BaseDAO {

    public AuthPassword getUserPassword(UUID userId) {
        return this.getMembershipAccess().get("membership.auth_password.getByUserId", userId);
    }

    public int insert(AuthPassword password) {
        return this.getMembershipAccess().insert("membership.auth_password.insert", password);
    }

    public int update(AuthPassword password) {
        return this.getMembershipAccess().update("membership.auth_password.update", password);
    }
}