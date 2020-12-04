package com.soonsoft.uranus.security.jwt.token;

import java.util.Collection;

import com.soonsoft.uranus.security.jwt.IApiToken;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SessionAuthenticationToken extends UsernamePasswordAuthenticationToken implements IApiToken {

    private static final long serialVersionUID = -5507466021750148792L;

    public SessionAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public SessionAuthenticationToken(Object principal, Object credentials, 
        Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    @Override
    public String getToken() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
