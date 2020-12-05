package com.soonsoft.uranus.security.config.api.configurer;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;
import com.soonsoft.uranus.security.jwt.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.jwt.ITokenProvider;
import com.soonsoft.uranus.security.jwt.ITokenStrategy;
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
        this(sessionIdHeaderName, requestHook, null);
    }

    public JWTConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.requestHook = requestHook;
        this.securityContextRepository = repo;
    }

    @Override
    public void config(HttpSecurity http) {
        ITokenProvider<?> tokenProvider = null;
        http.addFilterAt(
                new JWTSecurityContextPersistenceFilter(tokenProvider, securityContextRepository), 
                SecurityContextPersistenceFilter.class);

        try {
            http.apply(new WebApiLoginConfigurer<>((ITokenStrategy) tokenProvider));
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }
    
}
