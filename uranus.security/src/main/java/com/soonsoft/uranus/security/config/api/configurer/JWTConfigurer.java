package com.soonsoft.uranus.security.config.api.configurer;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.jwt.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.jwt.JWTHttpSessionSecurityContextRepository;
import com.soonsoft.uranus.security.jwt.JWTSecurityContextPersistenceFilter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

public class JWTConfigurer implements ICustomConfigurer {

    private String sessionIdHeaderName;
    private IRealHttpServletRequestHook requestHook;
    private SecurityContextRepository securityContextRepository;

    public JWTConfigurer(IRealHttpServletRequestHook requestHook) {
        this(null, requestHook);
    }

    public JWTConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook) {
        this(sessionIdHeaderName, requestHook, new JWTHttpSessionSecurityContextRepository());
    }

    public JWTConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.requestHook = requestHook;
        this.securityContextRepository = repo;
    }

    @Override
    public void config(HttpSecurity http) {
        http.addFilterAt(
                new JWTSecurityContextPersistenceFilter(sessionIdHeaderName, requestHook, securityContextRepository), 
                SecurityContextPersistenceFilter.class);
    }
    
}
