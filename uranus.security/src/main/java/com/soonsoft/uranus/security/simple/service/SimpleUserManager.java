package com.soonsoft.uranus.security.simple.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.soonsoft.uranus.core.error.UnsupportedException;
import com.soonsoft.uranus.core.model.data.IPagingList;
import com.soonsoft.uranus.core.model.data.PagingList;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.entity.PasswordInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.StatusConst.RoleStatus;
import com.soonsoft.uranus.security.entity.StatusConst.UserStatus;
import com.soonsoft.uranus.core.Guard;

import org.springframework.security.crypto.password.PasswordEncoder;


public class SimpleUserManager implements IUserManager {

    private PasswordEncoder passwordEncoder;

    private Map<String, UserInfo> userStore = new HashMap<>();

    public SimpleUserManager(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return the passwordEncoder
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void add(UserInfo userInfo) {
        if (userInfo != null) {
            userStore.put(userInfo.getUserName(), userInfo);
        }
    }

    public void addAll(Collection<UserInfo> users) {
        if (users != null) {
            for (UserInfo user : users) {
                add(user);
            }
        }
    }

    public void addAll(UserInfo[] users) {
        if (users != null) {
            for (int i = 0; i < users.length; i++) {
                add(users[i]);
            }
        }
    }

    @Override
    public UserInfo getUser(String username) {
        Guard.notEmpty(username, "username is required.");
        UserInfo user = userStore.get(username);
        if (user != null) {
            UserInfo copy = new UserInfo();
            user.copy(copy);
            return copy;
        }
        return user;
    }

    @Override
    public String encryptPassword(String password, String salt) {
        Guard.notEmpty(password, "the password is required.");

        if (salt == null) {
            salt = "";
        }

        String encodePassword = salt + password + salt;
        if (passwordEncoder != null) {
            encodePassword = passwordEncoder.encode(encodePassword);
        }
        return encodePassword;
    }

    //#region IUserManager Unsupported Methods

    @Override
    public boolean createUser(UserInfo user) {
        Guard.notNull(user, "the password is required.");

        user.setStatus(RoleStatus.ENABLED);
        add(user);
        return true;
    }

    @Override
    public boolean updateUser(UserInfo userInfo) {
        UserInfo old = userStore.get(userInfo.getUserName());
        if(old == null) {
            add(userInfo);
            return true;
        }

        userInfo.copy(old);
        return true;
    }

    @Override
    public boolean deleteUser(UserInfo user) {
        return userStore.remove(user.getUserName()) != null;
    }

    @Override
    public boolean deleteUser(String username) {
        return userStore.remove(username) != null;
    }

    @Override
    public boolean disableUser(UserInfo user) {
        UserInfo userInfo = userStore.get(user.getUserName());
        if(userInfo != null) {
            user.setStatus(UserStatus.DISABLED);
            return true;
        }
        return false;
    }

    @Override
    public void resetPassword(UserInfo user) {
        UserInfo userInfo = userStore.get(user.getUserName());
        if(userInfo != null) {
            user.setPassword("1", null);
        }
    }

    @Override
    public void changeMyPassword(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public void findMyPassword(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public PasswordInfo getEnabledPassword(String userId) {
        UserInfo userInfo = 
            userStore.values().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        if(userInfo != null) {
            return userInfo.getPasswordInfo();
        }
        return null;
    }

    @Override
    public IPagingList<UserInfo> queryUsers(Map<String, Object> params, int pageIndex, int pageSize) {
        List<UserInfo> result = userStore.values().stream().toList();
        PagingList<UserInfo> pageList = new PagingList<>(result);
        pageList.setPageIndex(pageIndex);
        pageList.setPageSize(pageSize);
        return pageList;
    }

    //#endregion
    
}