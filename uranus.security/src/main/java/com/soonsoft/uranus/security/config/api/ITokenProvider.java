package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

public interface ITokenProvider<T> {

    public final static String SESSION_ID_TYPE = "SESSION_ID";
    public final static String JWT_TYPE = "JWT";

    String getTokenType();

    ITokenStrategy<T> getTokenStrategy();

    boolean checkToken(HttpServletRequest request);

    Authentication refreshToken(String refreshToken);
    
}
