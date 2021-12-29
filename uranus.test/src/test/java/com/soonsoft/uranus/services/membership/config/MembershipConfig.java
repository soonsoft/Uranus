package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.EnableDatabaseAccess;
import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.membership.service.FunctionService;
import com.soonsoft.uranus.services.membership.service.RoleService;
import com.soonsoft.uranus.services.membership.service.UserService;
import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.dao.AuthPermissionDAO;
import com.soonsoft.uranus.services.membership.dao.AuthPrivilegeDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserRoleRelationDAO;
import com.soonsoft.uranus.services.membership.dao.SysFunctionDAO;
import com.soonsoft.uranus.services.membership.dao.SysMenuDAO;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootConfiguration
@Import(value = MembershipDataSourceProperty.class)
@EnableDatabaseAccess(primaryName = "membership", entityClassPackages = "com.soonsoft.uranus.services.membership.po")
public class MembershipConfig {

    @Bean(name = "membership")
    public DataSource dataSource(MembershipDataSourceProperty membershipDataSourceProperty) {
        return DataSourceFactory.create(membershipDataSourceProperty);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserService userService(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess, PasswordEncoder passwordEncoder) {
        AuthUserDAO authUserDAO = new AuthUserDAO(securityAccess);
        AuthPasswordDAO authPasswordDAO = new AuthPasswordDAO(securityAccess);
        AuthUserRoleRelationDAO userRoleRelationDAO = new AuthUserRoleRelationDAO(securityAccess);
        AuthPrivilegeDAO privilegeDAO = new AuthPrivilegeDAO(securityAccess);

        UserService userService = new UserService(authUserDAO, authPasswordDAO, userRoleRelationDAO, privilegeDAO);
        userService.setPasswordEncoder(passwordEncoder);

        return userService;
    }

    @Bean("membershipRoleService")
    public RoleService roleService(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess) {
        AuthRoleDAO roleDAO = new AuthRoleDAO(securityAccess);
        AuthUserRoleRelationDAO userRoleRelationDAO = new AuthUserRoleRelationDAO(securityAccess);
        AuthPermissionDAO permissionDAO = new AuthPermissionDAO(securityAccess);
        
        RoleService roleService = new RoleService(roleDAO, userRoleRelationDAO, permissionDAO);

        return roleService;
    }

    @Bean
    public FunctionService functionService(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess) {
        SysFunctionDAO functionDAO = new SysFunctionDAO(securityAccess);
        SysMenuDAO menuDAO = new SysMenuDAO(securityAccess);
        AuthPermissionDAO permissionDAO = new AuthPermissionDAO(securityAccess);

        FunctionService functionService = new FunctionService(functionDAO, menuDAO, permissionDAO);
        
        return functionService;
    }

}