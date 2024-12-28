package com.soonsoft.uranus.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;

import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.entity.UserInfo;

public class UserLoginFunction {

    private Func3<String, String, IUserManager, UserInfo> loginPasswordFn;

    private Func3<String, String, IUserManager, UserInfo> loginEmailVerifyCodeFn;

    private Func4<String, String, String, IUserManager, UserInfo> loginCellPhoneVerifyCodeFn;

    public void setLoginPasswordFn(Func3<String, String, IUserManager, UserInfo> loginPasswordFn) {
        this.loginPasswordFn = loginPasswordFn;
    }

    public Func3<String, String, IUserManager, UserInfo> getLoginPasswordFn() {
        return loginPasswordFn;
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, UserInfo> loginEmailVerifyCodeFn) {
        this.loginEmailVerifyCodeFn = loginEmailVerifyCodeFn;
    }

    public Func3<String, String, IUserManager, UserInfo> getLoginEmailVerifyCodeFn() {
        return loginEmailVerifyCodeFn;
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, UserInfo> loginCellPhoneVerifyCodeFn) {
        this.loginCellPhoneVerifyCodeFn = loginCellPhoneVerifyCodeFn;
    }

    public Func4<String, String, String, IUserManager, UserInfo> getLoginCellPhoneVerifyCodeFn() {
        return loginCellPhoneVerifyCodeFn;
    }

    public UserInfo loginPassword(String userName, String password, IUserManager userManager) {
        if(loginPasswordFn == null) {
            throw new BadCredentialsException("not support login by password.");
        }

        return loginPasswordFn.call(userName, password, userManager);
    }

    public UserInfo loginVerifyCodeWithEmail(String email, String verifyCode, IUserManager userManager) {
        if(loginEmailVerifyCodeFn == null) {
            throw new BadCredentialsException("not support login by verify-code with email.");
        }

        return loginEmailVerifyCodeFn.call(email, verifyCode, userManager);
    }

    public UserInfo loginVerifyCodeWithCellPhone(String areaCode, String cellphone, String verifyCode, IUserManager userManager) {
        if(loginCellPhoneVerifyCodeFn == null) {
            throw new BadCredentialsException("not support login by verify-code with cellphone.");
        }

        return loginCellPhoneVerifyCodeFn.call(areaCode, cellphone, verifyCode, userManager);
    }
    
}
