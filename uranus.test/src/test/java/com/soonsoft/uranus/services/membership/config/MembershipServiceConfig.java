package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;
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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.soonsoft.uranus.services.membership")
public class MembershipServiceConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserService userService(@Qualifier("membershipAccess") IDatabaseAccess securityAccess, PasswordEncoder passwordEncoder) {
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

    @Bean
    public RoleService roleService(@Qualifier("membershipAccess") IDatabaseAccess securityAccess) {
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

    @Bean
    public FunctionService functionService(@Qualifier("membershipAccess") IDatabaseAccess securityAccess) {
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