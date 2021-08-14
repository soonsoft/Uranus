package com.soonsoft.uranus.security.config.api.jwt;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.config.api.ITokenProvider;
import com.soonsoft.uranus.security.config.api.ITokenStrategy;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTAuthenticationToken;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class JWTTokenProvider implements ITokenProvider<JWTAuthenticationToken> {

    private final Logger LOGGER = LoggerFactory.getLogger(JWTTokenProvider.class); 

    private final String tokenHeaderName;
    private final ITokenStrategy<JWTAuthenticationToken> tokenStrategy;

    public JWTTokenProvider(String tokenHeaderName, ITokenStrategy<JWTAuthenticationToken> tokenStrategy) {
        this.tokenHeaderName = tokenHeaderName;
        this.tokenStrategy = tokenStrategy;
    }

    //#region ITokenProvider

    @Override
    public String getTokenType() {
        return JWT_TYPE;
    }

    @Override
    public ITokenStrategy<JWTAuthenticationToken> getTokenStrategy() {
        return tokenStrategy;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        String accessToken = StringUtils.isEmpty(tokenHeaderName) ? null : request.getHeader(tokenHeaderName);
        return !StringUtils.isEmpty(accessToken);
    }

    @Override
    public Authentication refreshToken(String refreshToken) {
        IUserManager userManager = SecurityManager.current().getUserManager();
        if(userManager == null) {
            return null;
        }

        String username = null;

        try {
            DecodedJWT jwt = JWT.decode(refreshToken);
            username = jwt.getClaim("username").asString();
        } catch(JWTDecodeException e) {
            LOGGER.warn("the refreshToken {} decode failed.", refreshToken);
            return null;
        }

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

    //#endregion

    public String getAccessToken(HttpServletRequest request) {
        return StringUtils.isEmpty(tokenHeaderName) ? null : request.getHeader(tokenHeaderName);
    }

}
