package com.soonsoft.uranus.security.authentication;

import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.core.Guard;

/**
 * IUserManager
 */
public interface IUserManager {

    UserInfo getUser(String username);

    boolean createUser(UserInfo user);

    boolean deleteUser(String username);

    default boolean deleteUser(UserInfo user) {
        Guard.notNull(user, "the UserInfo is required.");
        return deleteUser(user.getUsername());
    }

    boolean disableUser(UserInfo user);

    void resetPassword(UserInfo user);

    void changeMyPassword(UserInfo user);

    void findMyPassword(UserInfo user);

    String encryptPassword(String password, String salt);
}