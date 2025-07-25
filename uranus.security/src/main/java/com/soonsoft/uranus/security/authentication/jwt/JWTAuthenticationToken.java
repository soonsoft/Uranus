package com.soonsoft.uranus.security.authentication.jwt;

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
import com.soonsoft.uranus.security.entity.security.SecurityRole;
import com.soonsoft.uranus.security.entity.security.SecurityUser;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

// https://github.com/auth0/java-jwt
public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    // 默认 2小时
    private final static int DEFAULT_ACCESS_TOKEN_EXPIRE_MINUTES = 120;
    // 默认 15天
    private final static int DEFAULT_REFRESH_TOKEN_EXPIRE_MINUTES = 21600;

    private final Object principal;
    private final Date issuedTime;
    // AccessToken 有效时间，单位：分钟，默认值（2小时）
    private Date accessTokenExpireTime;
    // RefreshToken 有效时间，单位：分钟，默认值（15天）
    private Date refreshTokenExpireTime;

    private String secret = "uranus-security-secret";

    public JWTAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;

        this.issuedTime = new Date();
        setAccessTokenExpireTime(DEFAULT_ACCESS_TOKEN_EXPIRE_MINUTES);
        setRefreshTokenExpireTime(DEFAULT_REFRESH_TOKEN_EXPIRE_MINUTES);
    }

    public String getAccessToken() {
        Algorithm algorithm = Algorithm.HMAC512(secret);

        Map<String, Object> header = new HashMap<>();
        header.put(PublicClaims.TYPE, "JWT");
        header.put(PublicClaims.ALGORITHM, algorithm.getName());

        SecurityUser userInfo = (SecurityUser) getPrincipal();
        List<String> roles = null;
        if(userInfo.getAuthorities() != null) {
            roles = userInfo.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList());
        }
        
        String token = JWT.create()
            .withIssuedAt(issuedTime)
            .withExpiresAt(accessTokenExpireTime)
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

        SecurityUser userInfo = (SecurityUser) getPrincipal();
        
        String token = JWT.create()
            .withIssuedAt(issuedTime)
            .withExpiresAt(refreshTokenExpireTime)
            .withHeader(header)
            .withJWTId(userInfo.getUsername())
            .sign(algorithm);
            
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Date getAccessTokenExpireTime() {
        return accessTokenExpireTime;
    }

    public void setAccessTokenExpireTime(int accessTokenExpireMinutes) {
        int minutes = accessTokenExpireMinutes > 0 ? accessTokenExpireMinutes : DEFAULT_ACCESS_TOKEN_EXPIRE_MINUTES;
        this.accessTokenExpireTime = getExpireTime(minutes);
    }

    public void setAccessTokenExpireTime(Date accessTokenExpireTime) {
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    public Date getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(int refreshTokenExpireMinutes) {
        int minutes = refreshTokenExpireMinutes > 0 ? refreshTokenExpireMinutes : DEFAULT_REFRESH_TOKEN_EXPIRE_MINUTES;
        this.refreshTokenExpireTime = getExpireTime(minutes);
    }

    public void setRefreshTokenExpireTime(Date refreshTokenExpireTime) {
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    private Date getExpireTime(long minutes) {
        long expireTime = issuedTime.getTime() + minutes * 60L * 1000L;
        return new Date(expireTime);
    }

    public static JWTAuthenticationToken parse(String token) {
        if(StringUtils.isBlank(token)) {
            throw new JWTDecodeException("the jwtTokenString is null or empty.");
        }

        DecodedJWT jwt = JWT.decode(token);

        Date expiresAt = jwt.getExpiresAt();
        if(expiresAt != null) {
            if(expiresAt.getTime() < System.currentTimeMillis()) {
                return null;
            }
        }
            
        String username = jwt.getClaim("username").asString();
        SecurityUser userInfo = new SecurityUser(username);
        List<String> roles = jwt.getClaim("roles").asList(String.class);
        Collection<GrantedAuthority> authorities = null;
        if(roles != null) {
            authorities = roles.stream().map(i -> new SecurityRole(i)).collect(Collectors.toList());
        }

        JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(userInfo, authorities);
        jwtAuthenticationToken.setAccessTokenExpireTime(expiresAt);
        return jwtAuthenticationToken;
    }

}