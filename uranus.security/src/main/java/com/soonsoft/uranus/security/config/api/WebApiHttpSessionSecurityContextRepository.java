package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.jwt.ITokenProvider;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class WebApiHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

    private ITokenProvider<?> tokenProvider;

    public WebApiHttpSessionSecurityContextRepository(ITokenProvider<?> tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();

        boolean isSessionMode = tokenProvider.getSessionIdStrategy() != null;
        if(isSessionMode) {
            // API适配SessionId
            tokenProvider.getSessionIdStrategy().updateSessionId(request, response);
        } 
        
        SecurityContext securityContext = super.loadContext(requestResponseHolder);
        if(!isSessionMode && securityContext.getAuthentication() == null) {
            // API适配JWT-Token
            setJWTAuthentication(securityContext, request, response);
        }

        return securityContext;
    }

    protected void setJWTAuthentication(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        tokenProvider.checkToken(request);
        
        // TODO JWT anuthentication build.
        securityContext.setAuthentication(null);
    }
    
}
