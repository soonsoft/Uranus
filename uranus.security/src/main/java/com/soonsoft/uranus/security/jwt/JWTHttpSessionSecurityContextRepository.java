package com.soonsoft.uranus.security.jwt;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class JWTHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext securityContext = super.loadContext(requestResponseHolder);
        if(securityContext.getAuthentication() == null) {
            // TODO JWT anuthentication build.
        }
        return securityContext;
    }
    
}
