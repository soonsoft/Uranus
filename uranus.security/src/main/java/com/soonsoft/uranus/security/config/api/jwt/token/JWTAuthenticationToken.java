package com.soonsoft.uranus.security.config.api.jwt.token;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

// https://github.com/auth0/java-jwt
public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    // AccessToken 有效时间，单位：分钟，默认值（2小时）
    private int accessTokenExpireTime = 120;
    // RefreshToken 有效时间，单位：分钟，默认值（15天）
    private int refreshTokenExpireTime = 21600;

    private String secret = "uranus-security-secret";

    public JWTAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
    }

    public String getAccessToken() {
        Algorithm algorithm = Algorithm.HMAC512(secret);

        Map<String, Object> header = new HashMap<>();
        header.put(PublicClaims.TYPE, "JWT");
        header.put(PublicClaims.ALGORITHM, algorithm.getName());

        UserInfo userInfo = (UserInfo) getDetails();
        Collection<GrantedAuthority> authorities = userInfo.getAuthorities();
        List<String> roles = null;
        if(authorities != null) {
            roles = authorities.stream().map(i -> i.getAuthority()).collect(Collectors.toList());
        }
        
        String token = JWT.create()
            .withExpiresAt(getExpireTime(accessTokenExpireTime))
            .withHeader(header)
            .withClaim("userId", userInfo.getUserId())
            .withClaim("username", userInfo.getUsername())
            .withClaim("roles", roles)
            .sign(algorithm);
            
        return token;
    }

    public String getRefreshToken() {
        Algorithm algorithm = Algorithm.HMAC512(secret);

        Map<String, Object> header = new HashMap<>();
        header.put(PublicClaims.TYPE, "JWT");
        header.put(PublicClaims.ALGORITHM, algorithm.getName());

        UserInfo userInfo = (UserInfo) getDetails();
        
        String token = JWT.create()
            .withExpiresAt(getExpireTime(refreshTokenExpireTime))
            .withHeader(header)
            .withClaim("userId", userInfo.getUserId())
            .sign(algorithm);
            
        return token;
    }

    private static Date getExpireTime(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public static JWTAuthenticationToken parse(String token) {
        if(StringUtils.isBlank(token)) {
            throw new JWTDecodeException("the jwtTokenString is null or empty.");
        }

        DecodedJWT jwt = JWT.decode(token);
            
        String username = jwt.getClaim("username").asString();
        UserInfo userInfo = new UserInfo(username);
        List<String> roles = jwt.getClaim("roles").asList(String.class);
        Collection<GrantedAuthority> authorities = null;
        if(roles != null) {
            authorities = roles.stream().map(i -> new RoleInfo(i)).collect(Collectors.toList());
        }

        JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(username, authorities);
        jwtAuthenticationToken.setDetails(userInfo);
        return jwtAuthenticationToken;
    }

}