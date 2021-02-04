package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.dao.AuthRolesInFunctionsDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
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

        AuthRoleDAO roleDAO = new AuthRoleDAO();
        roleDAO.setMembershipAccess(securityAccess);
        AuthUsersInRolesDAO usersInRolesDAO = new AuthUsersInRolesDAO();
        usersInRolesDAO.setMembershipAccess(securityAccess);
        AuthRolesInFunctionsDAO rolesInFunctionsDAO = new AuthRolesInFunctionsDAO();
        rolesInFunctionsDAO.setMembershipAccess(securityAccess);
        
        RoleService roleService = new RoleService();
        roleService.setRoleDAO(roleDAO);
        roleService.setUsersInRolesDAO(usersInRolesDAO);
        roleService.setRolesInFunctionsDAO(rolesInFunctionsDAO);

        return roleService;
    }

    @Override
    public Class<?> getObjectType() {
        return RoleService.class;
    }
    
}
