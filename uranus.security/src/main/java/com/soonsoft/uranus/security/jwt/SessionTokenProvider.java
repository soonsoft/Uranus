package com.soonsoft.uranus.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.core.Authentication;

public class SessionTokenProvider implements ITokenProvider<String>, ITokenStrategy, ISessionIdStrategy {

    private String sessionIdHeaderName;
    private IRealHttpServletRequestHook httpRequestHook;

    public SessionTokenProvider(String sessionIdHeaderName, IRealHttpServletRequestHook httpRequestHook) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.httpRequestHook = httpRequestHook;
    }

    //#region ITokenProvider

    @Override
    public ITokenStrategy getTokenStrategy() {
        return this;
    }

    @Override
    public ISessionIdStrategy getSessionIdStrategy() {
        return this;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        return request != null && !StringUtils.isBlank(request.getHeader(sessionIdHeaderName));
    }

    @Override
    public void updateToken(HttpServletResponse response, String token) {
        response.setHeader(sessionIdHeaderName, token);
    }

    //#endregion

    //#region ITokenStrategy

    @Override
    public String getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        return request.getSession().getId();
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
