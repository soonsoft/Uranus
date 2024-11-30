package com.soonsoft.uranus.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;

import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.core.functional.func.Func4;
import com.soonsoft.uranus.security.entity.security.SecurityUser;

public class UserLoginFunction {

    private Func3<String, String, IUserManager, SecurityUser> loginPasswordFn;

    private Func3<String, String, IUserManager, SecurityUser> loginEmailVerifyCodeFn;

    private Func4<String, String, String, IUserManager, SecurityUser> loginCellPhoneVerifyCodeFn;

    public void setLoginPasswordFn(Func3<String, String, IUserManager, SecurityUser> loginPasswordFn) {
        this.loginPasswordFn = loginPasswordFn;
    }

    public void setLoginEmailVerifyCodeFn(Func3<String, String, IUserManager, SecurityUser> loginEmailVerifyCodeFn) {
        this.loginEmailVerifyCodeFn = loginEmailVerifyCodeFn;
    }

    public void setLoginCellPhoneVerifyCodeFn(Func4<String, String, String, IUserManager, SecurityUser> loginCellPhoneVerifyCodeFn) {
        this.loginCellPhoneVerifyCodeFn = loginCellPhoneVerifyCodeFn;
    }

    public SecurityUser loginPassword(String userName, String password, IUserManager userManager) {
        if(loginPasswordFn == null) {
            throw new BadCredentialsException("not support login by password.");
        }

        return loginPasswordFn.call(userName, password, userManager);
    }

    public SecurityUser loginVerifyCodeWithEmail(String email, String verifyCode, IUserManager userManager) {
        if(loginEmailVerifyCodeFn == null) {
            throw new BadCredentialsException("not support login by verify-code with email.");
        }

        return loginEmailVerifyCodeFn.call(email, verifyCode, userManager);
    }

    public SecurityUser loginVerifyCodeWithCellPhone(String areaCode, String cellphone, String verifyCode, IUserManager userManager) {
        if(loginCellPhoneVerifyCodeFn == null) {
            throw new BadCredentialsException("not support login by verify-code with cellphone.");
        }

        return loginCellPhoneVerifyCodeFn.call(areaCode, cellphone, verifyCode, userManager);
    }
    
}
