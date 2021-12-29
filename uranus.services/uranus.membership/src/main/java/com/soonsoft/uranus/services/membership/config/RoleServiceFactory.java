package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.dao.AuthPermissionDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserRoleRelationDAO;
import com.soonsoft.uranus.services.membership.service.RoleService;

import org.springframework.context.ApplicationContext;

public class RoleServiceFactory extends BaseMembershipServiceFactory<RoleService> {

    public RoleServiceFactory(IDatabaseAccess<?> membershipDatabaseAccess) {
        super(membershipDatabaseAccess);
    }

    public RoleServiceFactory(ApplicationContext context, String dbaBeanName) {
        super(context, dbaBeanName);
    }

    @Override
    public RoleService getObject() throws Exception {
        IDatabaseAccess<?> securityAccess = getDatabaseAccess();

        AuthRoleDAO roleDAO = new AuthRoleDAO(securityAccess);
        AuthUserRoleRelationDAO userRoleRelationDAO = new AuthUserRoleRelationDAO(securityAccess);
        AuthPermissionDAO permissionDAO = new AuthPermissionDAO(securityAccess);
        
        RoleService roleService = new RoleService(roleDAO, userRoleRelationDAO, permissionDAO);

        return roleService;
    }

    @Override
    public Class<?> getObjectType() {
        return RoleService.class;
    }
    
}
