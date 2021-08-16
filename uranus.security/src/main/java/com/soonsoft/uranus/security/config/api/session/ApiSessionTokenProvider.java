package com.soonsoft.uranus.security.config.api.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.api.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.config.api.ISessionIdStrategy;
import com.soonsoft.uranus.security.config.api.ITokenProvider;
import com.soonsoft.uranus.security.config.api.ITokenStrategy;

import org.springframework.security.core.Authentication;

public class ApiSessionTokenProvider implements ITokenProvider<String>, ITokenStrategy<String>, ISessionIdStrategy {

    private String sessionIdHeaderName;
    private IRealHttpServletRequestHook httpRequestHook;

    public ApiSessionTokenProvider(String sessionIdHeaderName, IRealHttpServletRequestHook httpRequestHook) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.httpRequestHook = httpRequestHook;
    }

    //#region ITokenProvider

    @Override
    public String getTokenType() {
        return SESSION_ID_TYPE;
    }

    @Override
    public ITokenStrategy<String> getTokenStrategy() {
        return this;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        return request != null && !StringUtils.isBlank(request.getHeader(sessionIdHeaderName));
    }

    //#endregion

    //#region ITokenStrategy

    @Override
    public String getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return request.getSession().getId();
    }

    @Override
    public boolean checkRefreshToken(String token) {
        // Session 模式下无效
        return false;
    }

    @Override
    public void updateRefreshToken(String token) {
        // Session 模式下无效
    }

    @Override
    public String refreshToken(String refreshToken) {
        // Session 模式下无效
        return null;
    }

    //#endregion

    //#region ISessionIdStrategy

    @Override
    public void updateSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = sessionIdHeaderName != null ? request.getHeader(sessionIdHeaderName) : null;
        if(sessionId == null || sessionId.length() == 0) {
            return;
        }

        if(httpRequestHook == null) {
            return;
        }

        HttpServletRequest realRequest = httpRequestHook.getRealHttpServletRequest(request);
        if(realRequest != null) {
            httpRequestHook.setSessionId(realRequest, sessionId);
        }

        response.setHeader(sessionIdHeaderName, sessionId);

    }

    //#endregion
    
}
