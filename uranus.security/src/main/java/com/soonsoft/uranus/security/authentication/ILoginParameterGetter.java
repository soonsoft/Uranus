package com.soonsoft.uranus.security.authentication;

import javax.servlet.http.HttpServletRequest;

public interface ILoginParameterGetter {

    default String getUserName(HttpServletRequest request) {
        return null;
    }

    default String getPassword(HttpServletRequest request) {
        return null;
    }

    default String getAreaCode(HttpServletRequest request) {
        return null;
    }

    default String getPhoneNumber(HttpServletRequest request) {
        return null;
    }

    default String getEmail(HttpServletRequest request) {
        return null;
    }

    default String getVerifyCode(HttpServletRequest request) {
        return null;
    }
    
}
