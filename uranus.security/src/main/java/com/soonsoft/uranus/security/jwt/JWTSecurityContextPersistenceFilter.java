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

    private String headerSessionIdName;

    private IRealHttpServletRequestHook requestHook;

    public JWTSecurityContextPersistenceFilter(IRealHttpServletRequestHook requestHook) {
        this(null, requestHook, new JWTHttpSessionSecurityContextRepository());
    }

    public JWTSecurityContextPersistenceFilter(IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        this(null, requestHook, repo);
    }

    public JWTSecurityContextPersistenceFilter(
        String headerSessionIdName, IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        super(repo);
        this.headerSessionIdName = headerSessionIdName;
        this.requestHook = requestHook;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        updateSessionId(request, response);
        
        super.doFilter(request, response, chain);
    }

    protected void updateSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = headerSessionIdName != null ? request.getHeader(headerSessionIdName) : null;
        if(sessionId == null || sessionId.length() == 0) {
            return;
        }

        if(requestHook == null) {
            return;
        }

        HttpServletRequest realRequest = requestHook.getRealHttpServletRequest(request);
        if(realRequest != null) {
            requestHook.setSessionId(realRequest, sessionId);
        }

        response.setHeader(headerSessionIdName, sessionId);
    }

}