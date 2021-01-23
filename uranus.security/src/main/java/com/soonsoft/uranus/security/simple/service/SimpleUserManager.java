package com.soonsoft.uranus.security.simple.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.soonsoft.uranus.core.error.UnsupportedException;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.entity.UserInfo;
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

    public void add(UserInfo user) {
        if (user != null) {
            userStore.put(user.getUsername(), user);
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
            UserInfo copy = new UserInfo(user.getUsername(), user.getPassword(), user.getAuthorities());
            copy.setCellPhone(user.getCellPhone());
            copy.setNickName(user.getNickName());
            copy.setPasswordSalt(user.getPasswordSalt());
            copy.setCreateTime(user.getCreateTime());
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

    //#region Unsupported Methods

    @Override
    public boolean createUser(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public boolean deleteUser(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public boolean deleteUser(String username) {
        throw new UnsupportedException();
    }

    @Override
    public boolean disableUser(UserInfo user) {
        return false;
    }

    @Override
    public void resetPassword(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public void changeMyPassword(UserInfo user) {
        throw new UnsupportedException();
    }

    @Override
    public void findMyPassword(UserInfo user) {
        throw new UnsupportedException();
    }

    //#endregion
    
}