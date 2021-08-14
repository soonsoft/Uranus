package com.soonsoft.uranus.services.membership.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.api.ITokenStorage;

public class DBRefreshTokenStorage implements ITokenStorage {

    @Override
    public boolean contains(String token) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void remove(String token) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(String token) {
        // TODO Auto-generated method stub
        
    }

    protected String getKey(String token) {
        if(StringUtils.isEmpty(token)) {
            throw new NullPointerException("the parameter token is null.");
        }

        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch(JWTDecodeException e) {
            return null;
        }
    }
    
}
