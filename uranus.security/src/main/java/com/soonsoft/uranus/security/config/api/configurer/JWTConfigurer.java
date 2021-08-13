package com.soonsoft.uranus.security.config.api.configurer;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.api.ITokenProvider;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;
import com.soonsoft.uranus.security.config.api.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTSimpleTokenStrategy;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 使用JWT模式，后端完全无状态，适合前后端分离单页应用
 */
public class JWTConfigurer implements ICustomConfigurer {

    private final static String DEFAULT_TOKEN_HEADER_NAME = "URANUS-AUTH_TOKEN";

    private String tokenHeaderName;
    private SecurityContextRepository securityContextRepository;

    public JWTConfigurer() {
        this(DEFAULT_TOKEN_HEADER_NAME, null);
    }

    public JWTConfigurer(String tokenHeaderName, SecurityContextRepository repo) {
        this.tokenHeaderName = tokenHeaderName;
        this.securityContextRepository = repo;
    }

    @Override
    public void config(HttpSecurity http) {
        ITokenProvider<?> tokenProvider = new JWTTokenProvider(tokenHeaderName, new JWTSimpleTokenStrategy());
        http.addFilterAt(
                new SecurityContextPersistenceFilter(securityContextRepository), 
                SecurityContextPersistenceFilter.class);

        try {
            http.apply(new WebApiLoginConfigurer<>(tokenProvider));
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }
    
}
