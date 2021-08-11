package com.soonsoft.uranus.security.config.api.configurer;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.api.WebApiHttpSessionSecurityContextRepository;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;
import com.soonsoft.uranus.security.config.api.WebApiSecurityContextPersistenceFilter;
import com.soonsoft.uranus.security.jwt.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.jwt.ITokenProvider;
import com.soonsoft.uranus.security.jwt.ITokenStrategy;
import com.soonsoft.uranus.security.jwt.SessionTokenProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

public class ApiSessionConfigurer implements ICustomConfigurer {

    private String sessionIdHeaderName;
    private IRealHttpServletRequestHook requestHook;
    private SecurityContextRepository securityContextRepository;

    public ApiSessionConfigurer(IRealHttpServletRequestHook requestHook) {
        this(null, requestHook);
    }

    public ApiSessionConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook) {
        this(sessionIdHeaderName, requestHook, null);
    }

    public ApiSessionConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.requestHook = requestHook;
        this.securityContextRepository = repo;
    }

    @Override
    public void config(HttpSecurity http) {
        ITokenProvider<?> tokenProvider = new SessionTokenProvider(sessionIdHeaderName, requestHook);
        if(securityContextRepository == null) {
            securityContextRepository = new WebApiHttpSessionSecurityContextRepository(tokenProvider);
        }

        http.addFilterAt(
                new WebApiSecurityContextPersistenceFilter(tokenProvider, securityContextRepository), 
                SecurityContextPersistenceFilter.class);

        try {
            http.apply(new WebApiLoginConfigurer<>((ITokenStrategy) tokenProvider));
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }
    
}
