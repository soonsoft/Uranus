package com.soonsoft.uranus.security.authentication;

import jakarta.servlet.http.HttpServletRequest;

public interface IRefreshTokenGetter {
    
    String getRefreshToken(HttpServletRequest request);

}
