package com.soonsoft.uranus.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

public class JWTSecurityContextPersistenceFilter extends SecurityContextPersistenceFilter {

    private ITokenProvider<?> tokenProvider;

    public JWTSecurityContextPersistenceFilter(ITokenProvider<?> tokenProvider, SecurityContextRepository repo) {
        super(repo);
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if(tokenProvider.getSessionIdStrategy() != null) {
            tokenProvider.getSessionIdStrategy().updateSessionId(request, response);
        }
        
        super.doFilter(request, response, chain);
    }

}