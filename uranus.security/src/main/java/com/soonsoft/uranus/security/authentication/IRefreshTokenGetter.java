package com.soonsoft.uranus.security.authentication;

import javax.servlet.http.HttpServletRequest;

public interface IRefreshTokenGetter {
    
    String getRefreshToken(HttpServletRequest request);

}
