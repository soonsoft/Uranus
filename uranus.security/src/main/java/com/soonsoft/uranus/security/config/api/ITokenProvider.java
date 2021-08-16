package com.soonsoft.uranus.security.config.api;

import javax.servlet.http.HttpServletRequest;

public interface ITokenProvider<T> {

    public final static String SESSION_ID_TYPE = "SESSION_ID";
    public final static String JWT_TYPE = "JWT";

    /**
     * 返回TokenType，用于区分是SessionIDToken 或是 JWTToken
     * @return "SESSION_ID" or "JWT"
     */
    String getTokenType();

    /**
     * 返回Token的策略，用于生成Token或者刷新Token
     * @return
     */
    ITokenStrategy<T> getTokenStrategy();

    /**
     * 检查Request对象中是否包含有效的accessToken
     * @param request HttpServletRequest对象
     * @return 包含有效的对象返回true，否则返回false
     */
    boolean checkToken(HttpServletRequest request);
    
}
