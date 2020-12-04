package com.soonsoft.uranus.security.jwt.token;

import com.soonsoft.uranus.security.jwt.IApiToken;
import com.soonsoft.uranus.security.jwt.ITokenStrategy;

public class SessionTokenStrategy implements ITokenStrategy {

    @Override
    public IApiToken createToken(String username, String password) {
        return null;
    }
    
}
