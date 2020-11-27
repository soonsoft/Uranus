package com.soonsoft.uranus.security.jwt;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public interface IRealHttpServletRequestHook {
    
    HttpServletRequest getRealHttpServletRequest(ServletRequest request);

    void setSessionId(HttpServletRequest request, String sessionId);

}
