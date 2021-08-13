package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;

public interface IRefreshTokenGetter {
    
    String getRefreshToken(HttpServletRequest request);

}
