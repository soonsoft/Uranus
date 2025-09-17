package com.soonsoft.uranus.security.config.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ISessionIdStrategy {

    void updateSessionId(HttpServletRequest request, HttpServletResponse response);
    
}
