package com.soonsoft.uranus.security.config.api.jwt.token;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.config.api.ITokenStorage;
import com.soonsoft.uranus.security.config.api.ITokenStrategy;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class JWTSimpleTokenStrategy implements ITokenStrategy<JWTAuthenticationToken> {

    private final Logger LOGGER = LoggerFactory.getLogger(JWTSimpleTokenStrategy.class); 

    private ITokenStorage tokenStorage;

    public JWTSimpleTokenStrategy(ITokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
    }

    @Override
    public JWTAuthenticationToken getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        JWTAuthenticationToken jwtAuthenticationToken = 
            authentication instanceof JWTAuthenticationToken 
                ? (JWTAuthenticationToken) authentication
                : new JWTAuthenticationToken(authentication.getPrincipal(), authentication.getAuthorities());
        return jwtAuthenticationToken;
    }

    @Override
    public String checkRefreshToken(String refreshToken) {
        String jti = null;
        if(this.tokenStorage != null) {
            try {
                DecodedJWT jwt = JWT.decode(refreshToken);
                Date expiresAt = jwt.getExpiresAt();
                if(expiresAt != null) {
                    if(expiresAt.getTime() < System.currentTimeMillis()) {
                        return null;
                    }
                }
                jti = jwt.getId();
                jti = StringUtils.isBlank(jti) ? null : jti;
                if(jti != null && tokenStorage.contains(jti, refreshToken)) {
                    tokenStorage.remove(jti);
                } else {
                    return null;
                }
            } catch(JWTDecodeException e) {
                LOGGER.warn("the refreshToken {} decode failed.", refreshToken);
                return null;
            }
        }
        return jti;
    }

    @Override
    public JWTAuthenticationToken refreshToken(String jti) {
        IUserManager userManager = SecurityManager.current().getUserManager();
        if(userManager == null) {
            return null;
        }

        String username = jti;

        if(StringUtils.isEmpty(username)) {
            return null;
        }

        try {
            UserInfo userInfo = userManager.getUser(username);
            JWTAuthenticationToken jwtAuthenticationToken = new JWTAuthenticationToken(userInfo, userInfo.getAuthorities());
            return jwtAuthenticationToken;
        } catch(Exception e) {
            LOGGER.error("get userInfo by username [" + username + "] failed.", e);
            return null;
        }
    }

    @Override
    public void updateRefreshToken(JWTAuthenticationToken jwtAuthenticationToken) {

        if(this.tokenStorage != null) {
            String jti = ((UserInfo) jwtAuthenticationToken.getPrincipal()).getUsername();
            this.tokenStorage.set(jti, jwtAuthenticationToken.getRefreshToken());
        }
        
    }
    
}
