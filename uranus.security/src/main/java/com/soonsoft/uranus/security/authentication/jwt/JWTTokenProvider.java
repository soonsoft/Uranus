package com.soonsoft.uranus.security.authentication.jwt;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authentication.ITokenProvider;
import com.soonsoft.uranus.security.authentication.ITokenStrategy;

import jakarta.servlet.http.HttpServletRequest;

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
