package com.soonsoft.uranus.site.service;

import org.springframework.stereotype.Service;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.entity.UserInfo;

@Service
public class LoginService {
    
    public UserInfo loginByPassword(String username, String password, IUserManager userManager) {
        Guard.notEmpty(username, "the arugments[username] is required.");

        UserInfo userInfo = userManager.getUser(username);
        if(userInfo != null && userManager.checkPassword(password, userInfo)) {
            return userInfo;
        }
        
        return null;
    }

}
