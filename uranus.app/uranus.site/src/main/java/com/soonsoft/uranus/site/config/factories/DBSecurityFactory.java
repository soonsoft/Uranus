package com.soonsoft.uranus.site.config.factories;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.services.membership.service.FunctionService;
import com.soonsoft.uranus.services.membership.service.RoleService;
import com.soonsoft.uranus.services.membership.service.UserService;
import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.dao.AuthRolesInFunctionsDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
import com.soonsoft.uranus.services.membership.dao.SysFunctionDAO;
import com.soonsoft.uranus.services.membership.dao.SysMenuDAO;

import org.springframework.security.crypto.password.PasswordEncoder;


public class DBSecurityFactory {

    public IUserManager createUserManager(IDatabaseAccess<?> securityAccess, PasswordEncoder passwordEncoder) {
        AuthUserDAO authUserDAO = new AuthUserDAO();
        authUserDAO.setMembershipAccess(securityAccess);
        AuthPasswordDAO authPasswordDAO = new AuthPasswordDAO();
        authPasswordDAO.setMembershipAccess(securityAccess);
        AuthUsersInRolesDAO usersInRolesDAO = new AuthUsersInRolesDAO();
        usersInRolesDAO.setMembershipAccess(securityAccess);

        UserService userService = new UserService();
        userService.setUserDAO(authUserDAO);
        userService.setPasswordDAO(authPasswordDAO);
        userService.setUsersInRolesDAO(usersInRolesDAO);
        userService.setPasswordEncoder(passwordEncoder);

        return userService;
    }

    public IRoleManager createRoleManager(IDatabaseAccess<?> securityAccess) {
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

    public IFunctionManager createFunctionManager(IDatabaseAccess<?> securityAccess) {
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
    
}