package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * 登录成功以后的Token适配器
 */
public interface ITokenStrategy<T> {
    
    /**
     * 返回AuthenticationToken 对象，用于登录完成返回给前端
     * @param request HttpServletRequest 对象
     * @param response HttpServletResponse 对象
     * @param authentication SpringSecurity Token
     * @return 返回不同类型的Token
     */
    T getToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    /**
     * 检查token是否有效
     * @param token
     * @return 有效返回true，无效返回false
     */
    boolean checkToken(String token);

    /**
     * 更新缓存中的token信息
     * @param token token
     */
    void updateToken(String token);

    /**
     * 刷新 Token
     * @param token token信息
     * @return
     */
    T refreshToken(String token);

}
