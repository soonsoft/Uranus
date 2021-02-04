package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.membership.dao.AuthRolesInFunctionsDAO;
import com.soonsoft.uranus.services.membership.dao.SysFunctionDAO;
import com.soonsoft.uranus.services.membership.dao.SysMenuDAO;
import com.soonsoft.uranus.services.membership.service.FunctionService;

import org.springframework.context.ApplicationContext;

public class FunctionServiceFactory extends BaseMembershipServiceFactory<FunctionService> {

    public FunctionServiceFactory(IDatabaseAccess<?> membershipDatabaseAccess) {
        super(membershipDatabaseAccess);
    }

    public FunctionServiceFactory(ApplicationContext context, String dbaBeanName) {
        super(context, dbaBeanName);
    }

    @Override
    public FunctionService getObject() throws Exception {
        IDatabaseAccess<?> securityAccess = getDatabaseAccess();

        SysFunctionDAO functionDAO = new SysFunctionDAO();
        functionDAO.setMembershipAccess(securityAccess);
        SysMenuDAO menuDAO = new SysMenuDAO();
        menuDAO.setMembershipAccess(securityAccess);
        AuthRolesInFunctionsDAO rolesInFunctionsDAO = new AuthRolesInFunctionsDAO();
        rolesInFunctionsDAO.setMembershipAccess(securityAccess);

        FunctionService functionService = new FunctionService();
        functionService.setFunctionDAO(functionDAO);
        functionService.setMenuDAO(menuDAO);
        functionService.setRolesInFunctionsDAO(rolesInFunctionsDAO);
        
        return functionService;
    }

    @Override
    public Class<?> getObjectType() {
        return FunctionService.class;
    }
    
}
