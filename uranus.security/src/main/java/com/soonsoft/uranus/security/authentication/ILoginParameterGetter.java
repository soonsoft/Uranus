package com.soonsoft.uranus.security.authentication;

import javax.servlet.http.HttpServletRequest;

public interface ILoginParameterGetter {

    String getUserName(HttpServletRequest request);

    String getPassword(HttpServletRequest request);

    String getAreaCode(HttpServletRequest request);

    String getPhoneNumber(HttpServletRequest request);

    String getEmail(HttpServletRequest request);

    String getVerifyCode(HttpServletRequest request);
    
}
