package com.soonsoft.uranus.security.jwt;

import java.util.Collection;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken implements IApiToken {

    private static final long serialVersionUID = 2129745000917232643L;

    public JWTAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JWTAuthenticationToken(Object principal, Object credentials, 
        Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static String stringify(JWTAuthenticationToken jwtToken) {
        // Algorithm algorithm = Algorithm.HMAC256("secret");
        // String token = JWT.create()
        //     .withHeader(null)
        //     .withClaim("userId", "")
        //     .sign(algorithm);
        // return token;
        return null;
    }

    public static JWTAuthenticationToken parse(String jwtTokenString) {
        if(StringUtils.isBlank(jwtTokenString)) {
            return null;
        }

        return null;
    }

    @Override
    public String getToken() {
        return stringify(this);
    }

}