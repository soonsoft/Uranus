package com.soonsoft.uranus.security.jwt;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * JWTAuthenticationToken
 */
public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 2129745000917232643L;

    public JWTAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JWTAuthenticationToken(Object principal, Object credentials, 
        Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

}