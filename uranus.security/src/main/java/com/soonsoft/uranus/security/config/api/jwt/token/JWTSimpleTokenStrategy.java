package com.soonsoft.uranus.security.config.api.jwt.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.api.ITokenStorage;
import com.soonsoft.uranus.security.config.api.ITokenStrategy;

import org.springframework.security.core.Authentication;

public class JWTSimpleTokenStrategy implements ITokenStrategy<JWTAuthenticationToken> {

    private ITokenStorage tokenStorage;

    public JWTSimpleTokenStrategy(ITokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @Override
    public JWTAuthenticationToken getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        JWTAuthenticationToken jwtAuthenticationToken = 
            authentication instanceof JWTAuthenticationToken 
                ? (JWTAuthenticationToken) authentication
                : new JWTAuthenticationToken(authentication.getPrincipal(), authentication.getAuthorities());
        return jwtAuthenticationToken;
    }

    @Override
    public boolean checkRefreshToken(String token) {
        return this.tokenStorage == null ? true : tokenStorage.contains(token);
    }

    @Override
    public void updateRefreshToken(String token) {
        if(this.tokenStorage != null) {
            this.tokenStorage.put(token);
        }
        
    }
    
}
