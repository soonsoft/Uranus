package com.soonsoft.uranus.security.config.api.jwt.token;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

// TODO 完成JWT Token转换
public class JWTAuthenticationToken {

    public JWTAuthenticationToken(Object principal, Object credentials) {
    }

    public JWTAuthenticationToken(Object principal, Object credentials, 
        Collection<? extends GrantedAuthority> authorities) {
    }

    public String getAccessToken() {
        return null;
    }

    public String getRefreshToken() {
        return null;
    }

    public Authentication getAuthentication() {
        return null;
    }

    public static String stringify(JWTAuthenticationToken jwtToken) {
        // https://github.com/auth0/java-jwt
        Algorithm algorithm = Algorithm.HMAC512("uranus-security-secret");

        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", algorithm.getName());
        
        String token = JWT.create()
            .withHeader(header)
            .withClaim("userId", "")
            .sign(algorithm);
        return token;
    }

    public static JWTAuthenticationToken parse(String jwtTokenString) {
        if(StringUtils.isBlank(jwtTokenString)) {
            return null;
        }

        return null;
    }

}