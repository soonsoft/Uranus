package com.soonsoft.uranus.security.config.api.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.jwt.ISessionIdStrategy;
import com.soonsoft.uranus.security.jwt.ITokenProvider;
import com.soonsoft.uranus.security.jwt.ITokenStrategy;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class JWTTokenProvider implements ITokenProvider<String>, ITokenStrategy {

    //#region ITokenStrategy

    @Override
    public String getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public UsernamePasswordAuthenticationToken createAuthenticationToken() {
        // TODO Auto-generated method stub
        return null;
    }

    //#endregion

    //#region ITokenProvider

    @Override
    public ITokenStrategy getTokenStrategy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISessionIdStrategy getSessionIdStrategy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateToken(HttpServletResponse response, String token) {
        // TODO Auto-generated method stub
        
    }

    //#endregion
    
}
