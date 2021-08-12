package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.api.provider.JWTTokenProvider;
import com.soonsoft.uranus.security.jwt.ISessionIdStrategy;
import com.soonsoft.uranus.security.jwt.ITokenProvider;
import com.soonsoft.uranus.security.jwt.token.JWTAuthenticationToken;

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

        boolean isSessionMode = tokenProvider instanceof ISessionIdStrategy;
        if(isSessionMode) {
            // API适配SessionId
            ((ISessionIdStrategy) tokenProvider).updateSessionId(request, response);
        } 
        
        SecurityContext securityContext = super.loadContext(requestResponseHolder);

        // 如果Token无效，则清空登录信息
        if(!tokenProvider.checkToken(request)) {
            securityContext.setAuthentication(null);
            return securityContext;
        }

        if(!isSessionMode) {
            // API适配JWT-Token
            setJWTAuthentication(securityContext, request, response);
        }

        return securityContext;
    }

    protected void setJWTAuthentication(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
        
        String accessToken = ((JWTTokenProvider) tokenProvider).getAccessToken(request);
        if(accessToken == null) {
            return;
        }
        
        JWTAuthenticationToken authenticationToken = JWTAuthenticationToken.parse(accessToken);
        securityContext.setAuthentication(authenticationToken.getAuthentication());
    }
    
}
