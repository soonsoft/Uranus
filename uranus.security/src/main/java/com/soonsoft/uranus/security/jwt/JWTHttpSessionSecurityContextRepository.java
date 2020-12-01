package com.soonsoft.uranus.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class JWTHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext securityContext = super.loadContext(requestResponseHolder);
        if(securityContext.getAuthentication() == null) {
            setJWTAuthentication(
                securityContext, 
                requestResponseHolder.getRequest(), 
                requestResponseHolder.getResponse());
        }
        return securityContext;
    }

    protected void setJWTAuthentication(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        // TODO JWT anuthentication build.
        securityContext.setAuthentication(null);
    }
    
}
