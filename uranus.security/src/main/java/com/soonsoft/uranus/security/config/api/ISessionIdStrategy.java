package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISessionIdStrategy {

    void updateSessionId(HttpServletRequest request, HttpServletResponse response);
    
}
