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
    private Date accessTokenExpireTime;
    // RefreshToken 有效时间，单位：分钟，默认值（15天）
    private Date refreshTokenExpireTime;

    private String secret = "uranus-security-secret";

    public JWTAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.principal = principal;
        // 2小时
        setAccessTokenExpireTime(120);
        // 15天
        setRefreshTokenExpireTime(21600);
    }

    public String getAccessToken() {
        Algorithm algorithm = Algorithm.HMAC512(secret);

        Map<String, Object> header = new HashMap<>();
        header.put(PublicClaims.TYPE, "JWT");
        header.put(PublicClaims.ALGORITHM, algorithm.getName());

        UserInfo userInfo = (UserInfo) getPrincipal();
        Collection<GrantedAuthority> authorities = userInfo.getAuthorities();
        List<String> roles = null;
        if(authorities != null) {
            roles = authorities.stream().map(i -> i.getAuthority()).collect(Collectors.toList());
        }
        
        String token = JWT.create()
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

        UserInfo userInfo = (UserInfo) getPrincipal();
        
        String token = JWT.create()
            .withExpiresAt(refreshTokenExpireTime)
            .withHeader(header)
            .withClaim("username", userInfo.getUsername())
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
        this.accessTokenExpireTime = getExpireTime(accessTokenExpireMinutes);
    }

    public Date getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(int refreshTokenExpireMinutes) {
        this.refreshTokenExpireTime = getExpireTime(refreshTokenExpireMinutes);
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    private static Date getExpireTime(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
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

        JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(userInfo, authorities);
        return jwtAuthenticationToken;
    }

}