package com.soonsoft.uranus.security.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

public class LogoutEvent {

    private HttpServletRequest request;

    private HttpServletResponse response;
    
    private Authentication authentication;

    public LogoutEvent(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        this.request = request;
        this.response = response;
        this.authentication = authentication;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    

}