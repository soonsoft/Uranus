package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * 登录成功以后的Token适配器
 */
public interface ITokenStrategy<T> {
    
    /**
     * 返回Token
     * @param request HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     * @param authentication SpringSecurity Token
     * @return
     */
    T getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    boolean checkRefreshToken(String token);

    void updateRefreshToken(String token);

}
