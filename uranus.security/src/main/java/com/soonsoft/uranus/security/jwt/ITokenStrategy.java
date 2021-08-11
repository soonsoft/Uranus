package com.soonsoft.uranus.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public interface ITokenStrategy {
    
    String getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    String getToken(HttpServletRequest request);

    UsernamePasswordAuthenticationToken createAuthenticationToken();

}
