package com.soonsoft.uranus.security.config.api.jwt;

import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.api.ITokenProvider;
import com.soonsoft.uranus.security.config.api.ITokenStrategy;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTAuthenticationToken;

public class JWTTokenProvider implements ITokenProvider<JWTAuthenticationToken> {

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

    //#endregion

    public String getAccessToken(HttpServletRequest request) {
        return StringUtils.isEmpty(tokenHeaderName) ? null : request.getHeader(tokenHeaderName);
    }

}
