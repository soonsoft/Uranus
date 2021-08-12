package com.soonsoft.uranus.security.config.api.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.jwt.ITokenProvider;
import com.soonsoft.uranus.security.jwt.ITokenStrategy;
import com.soonsoft.uranus.security.jwt.token.JWTAuthenticationToken;

public class JWTTokenProvider implements ITokenProvider<JWTAuthenticationToken> {

    private final String tokenHeaderName;
    private final ITokenStrategy<JWTAuthenticationToken> tokenStrategy;

    public JWTTokenProvider(String tokenHeaderName, ITokenStrategy<JWTAuthenticationToken> tokenStrategy) {
        this.tokenHeaderName = tokenHeaderName;
        this.tokenStrategy = tokenStrategy;
    }

    //#region ITokenProvider

    @Override
    public ITokenStrategy<JWTAuthenticationToken> getTokenStrategy() {
        return tokenStrategy;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateToken(HttpServletResponse response, String token) {
        // TODO Auto-generated method stub
        
    }

    //#endregion

    public String getAccessToken(HttpServletRequest request) {
        return StringUtils.isEmpty(tokenHeaderName) ? null : request.getHeader(tokenHeaderName);
    }

}
