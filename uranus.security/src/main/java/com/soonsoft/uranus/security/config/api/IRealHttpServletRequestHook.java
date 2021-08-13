package com.soonsoft.uranus.security.config.api;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public interface IRealHttpServletRequestHook {
    
    HttpServletRequest getRealHttpServletRequest(ServletRequest request);

    void setSessionId(HttpServletRequest request, String sessionId);

}
