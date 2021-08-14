package com.soonsoft.uranus.security.config.api.jwt.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.api.ITokenStrategy;

import org.springframework.security.core.Authentication;

public class JWTSimpleTokenStrategy implements ITokenStrategy<JWTAuthenticationToken> {

    @Override
    public JWTAuthenticationToken getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(authentication.getPrincipal(), authentication.getAuthorities());
        return jwtAuthenticationToken;
    }

    @Override
    public boolean checkToken(String token) {
        // TODO 检查refreshToken是不是有效，refreshToken是一次性的
        return true;
    }

    @Override
    public void updateToken(String token) {
        // TODO 更新refreshToken
        
    }
    
}
