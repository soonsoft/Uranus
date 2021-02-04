package com.soonsoft.uranus.services.membership.dao;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.core.Guard;

/**
 * BaseDAO
 */
public abstract class BaseDAO {

    private IDatabaseAccess<?> membershipAccess;

    public BaseDAO() {

    }

    public IDatabaseAccess<?> getMembershipAccess() {
        return this.membershipAccess;
    }

    public void setMembershipAccess(IDatabaseAccess<?> securityAccess) {
        Guard.notNull(securityAccess, "the IDatabaseAccess is required.");
        this.membershipAccess = securityAccess;
    }

    
}