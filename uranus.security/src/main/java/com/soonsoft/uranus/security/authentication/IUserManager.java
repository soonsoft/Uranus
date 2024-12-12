package com.soonsoft.uranus.security.authentication;

import com.soonsoft.uranus.security.entity.PasswordInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.model.data.IPagingList;

public interface IUserManager {

    IPagingList<UserInfo> queryUsers(Map<String, Object> params, int pageIndex, int pageSize);

    UserInfo getUser(String username);

    UserInfo getUserByCellPhone(String areaCode, String cellPhone);

    boolean checkPassword(String password, UserInfo userInfo);

    boolean createUser(UserInfo userInfo);

    boolean updateUser(UserInfo userInfo);

    boolean deleteUser(String username);

    default boolean deleteUser(UserInfo user) {
        Guard.notNull(user, "the UserInfo is required.");
        return deleteUser(user.getUserName());
    }

    boolean disableUser(UserInfo user);

    PasswordInfo getEnabledPassword(String userId);

    void resetPassword(UserInfo user);

    void changeMyPassword(UserInfo user);

    void findMyPassword(UserInfo user);

    String encryptPassword(String password, String salt);
}