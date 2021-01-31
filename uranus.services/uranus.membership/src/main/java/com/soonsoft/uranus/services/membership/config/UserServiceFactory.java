package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
import com.soonsoft.uranus.services.membership.service.UserService;

import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceFactory extends BaseMembershipServiceFactory<UserService> {

    private PasswordEncoder passwordEncoder;

    public UserServiceFactory(IDatabaseAccess membershipDatabaseAccess, PasswordEncoder passwordEncoder) {
        super(membershipDatabaseAccess);
        this.passwordEncoder = passwordEncoder;
    }

    public UserServiceFactory(ApplicationContext context, String dbaBeanName, PasswordEncoder passwordEncoder) {
        super(context, dbaBeanName);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserService getObject() throws Exception {
        IDatabaseAccess securityAccess = getDatabaseAccess();

        AuthUserDAO userDAO = new AuthUserDAO();
        userDAO.setMembershipAccess(securityAccess);

        AuthPasswordDAO passwordDAO = new AuthPasswordDAO();
        passwordDAO.setMembershipAccess(securityAccess);

        AuthUsersInRolesDAO usersInRolesDAO = new AuthUsersInRolesDAO();
        usersInRolesDAO.setMembershipAccess(securityAccess);

        UserService userService = new UserService();
        userService.setUserDAO(userDAO);
        userService.setPasswordDAO(passwordDAO);
        userService.setUsersInRolesDAO(usersInRolesDAO);
        userService.setPasswordEncoder(passwordEncoder);

        return userService;
    }

    @Override
    public Class<UserService> getObjectType() {
        return UserService.class;
    }
    
}
