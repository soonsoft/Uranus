package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public interface ITokenStrategy<T> {
    
    T getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

}
