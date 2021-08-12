package com.soonsoft.uranus.security.jwt.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.jwt.ITokenStrategy;

import org.springframework.security.core.Authentication;

public class JWTSimpleTokenStrategy implements ITokenStrategy<JWTAuthenticationToken> {

    @Override
    public JWTAuthenticationToken getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
