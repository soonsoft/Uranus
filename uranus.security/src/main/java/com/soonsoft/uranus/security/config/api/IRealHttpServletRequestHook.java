package com.soonsoft.uranus.security.config.api;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface IRealHttpServletRequestHook {
    
    HttpServletRequest getRealHttpServletRequest(ServletRequest request);

    void setSessionId(HttpServletRequest request, String sessionId);

}
