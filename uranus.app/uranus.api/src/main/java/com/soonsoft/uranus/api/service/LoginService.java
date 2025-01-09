package com.soonsoft.uranus.api.service;

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

    public UserInfo loginByVerifyCode(String areaCode, String cellPhone, String verifyCode, IUserManager userManager) {
        Guard.notEmpty(areaCode, "the arugments[areaCode] is required.");
        Guard.notEmpty(cellPhone, "the arugments[cellPhone] is required.");

        UserInfo userInfo = userManager.getUserByCellPhone(areaCode, cellPhone);
        if(userInfo != null && "123456".equals(verifyCode)) {
            return userInfo;
        }
        
        return null;
    }

    public UserInfo loginByVerifyCode(String email, String verifyCode, IUserManager userManager) {
        Guard.notEmpty(email, "the arugments[email] is required.");

        UserInfo userInfo = userManager.getUserByEmail(email);
        if(userInfo != null && "123456".equals(verifyCode)) {
            return userInfo;
        }
        
        return null;
    }

}
