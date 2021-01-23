package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
import com.soonsoft.uranus.services.membership.service.UserService;

import org.springframework.beans.factory.FactoryBean;

public class UserServiceFactory implements FactoryBean<UserService> {

    @Override
    public UserService getObject() throws Exception {
        AuthUserDAO userDAO = new AuthUserDAO();

        AuthPasswordDAO passwordDAO = new AuthPasswordDAO();

        AuthUsersInRolesDAO usersInRolesDAO = new AuthUsersInRolesDAO();


        UserService userService = new UserService();
        userService.setUserDAO(userDAO);
        userService.setPasswordDAO(passwordDAO);
        userService.setUsersInRolesDAO(usersInRolesDAO);

        return userService;
    }

    @Override
    public Class<UserService> getObjectType() {
        return UserService.class;
    }
    
}
