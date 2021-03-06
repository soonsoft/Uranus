package com.soonsoft.uranus.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ITokenProvider<T> {

    ITokenStrategy getTokenStrategy();

    ISessionIdStrategy getSessionIdStrategy();

    boolean checkToken(HttpServletRequest request);

    void updateToken(HttpServletResponse response, String token);
    
}
