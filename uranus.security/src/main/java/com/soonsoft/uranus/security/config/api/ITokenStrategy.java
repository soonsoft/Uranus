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
     * 检查refresh-token是否有效
     * 【注意】refresh-token有效期很长，但它是一次性的，只能验证一次，一定要确保验证后就失效
     * @param token
     * @return 有效返回true，无效返回false
     */
    boolean checkRefreshToken(String refreshToken);

    /**
     * 刷新refresh-token
     * @param token token信息
     * @return 返回新的返回AuthenticationToken
     */
    T refreshToken(String token);

    /**
     * 更新refresh-token，旧refresh-token会失效
     * @param token 返回AuthenticationToken 对象
     */
    void updateRefreshToken(T token);

}
