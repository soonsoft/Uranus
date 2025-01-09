package com.soonsoft.uranus.security.config.api.jwt;

import com.soonsoft.uranus.security.authentication.ILoginParameterGetter;
import com.soonsoft.uranus.security.authentication.ITokenProvider;
import com.soonsoft.uranus.security.authentication.ITokenStorage;
import com.soonsoft.uranus.security.authentication.jwt.JWTSimpleTokenStrategy;
import com.soonsoft.uranus.security.authentication.jwt.JWTTokenProvider;
import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;
import com.soonsoft.uranus.security.config.api.WebApiSecurityContextHolderFilter;
import com.soonsoft.uranus.security.config.api.WebApiSecurityContextHolderFilter.WebApiHttpSessionSecurityContextRepository;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 使用JWT模式，后端完全无状态，适合前后端分离单页应用
 */
public class JWTConfigurer implements ICustomConfigurer {

    private String tokenHeaderName;
    private SecurityContextRepository securityContextRepository;
    private ITokenStorage tokenStorage;

    public JWTConfigurer(String tokenHeaderName) {
        this(tokenHeaderName, null, null);
    }

    public JWTConfigurer(String tokenHeaderName, ITokenStorage tokenStorage) {
        this(tokenHeaderName, null, tokenStorage);
    }

    public JWTConfigurer(String tokenHeaderName, SecurityContextRepository repo) {
        this(tokenHeaderName, repo, null);
    }

    public JWTConfigurer(String tokenHeaderName, SecurityContextRepository repo, ITokenStorage tokenStorage) {
        this.tokenHeaderName = tokenHeaderName;
        this.securityContextRepository = repo;
        this.tokenStorage = tokenStorage;
    }

    @Override
    public void config(HttpSecurity http, WebApplicationSecurityConfig config) {
        ITokenProvider<?> tokenProvider = new JWTTokenProvider(tokenHeaderName, new JWTSimpleTokenStrategy(tokenStorage));

        if(securityContextRepository == null) {
            securityContextRepository = new WebApiHttpSessionSecurityContextRepository(tokenProvider);
        }

        // 添加身份信息处理Filter
        http.addFilterAt(
                new WebApiSecurityContextHolderFilter(securityContextRepository), 
                SecurityContextHolderFilter.class);
        // 添加Login处理Filter
        try {
            WebApiLoginConfigurer loginConfigurer = new WebApiLoginConfigurer(tokenProvider);

            loginConfigurer.setLoginPasswordUrl(SecurityConfigUrlConstant.LoginPasswordUrl);
            loginConfigurer.setLoginVerifyCodeByCellPhoneUrl(SecurityConfigUrlConstant.LoginVerifyCodeCellPhoneUrl);
            loginConfigurer.setLoginVerifyCodeByEmailUrl(SecurityConfigUrlConstant.LoginVerifyCodeEmailUrl);
            loginConfigurer.setLoginTokenRefreshUrl(SecurityConfigUrlConstant.LoginTokenRefreshUrl);

            loginConfigurer.setLoginPasswordFn(config.getUserLoginFunction().getLoginPasswordFn());
            loginConfigurer.setLoginCellPhoneVerifyCodeFn(config.getUserLoginFunction().getLoginCellPhoneVerifyCodeFn());
            loginConfigurer.setLoginEmailVerifyCodeFn(config.getUserLoginFunction().getLoginEmailVerifyCodeFn());

            loginConfigurer.setLoginParameterGetter(new ILoginParameterGetter() {
                @Override
                public String getUserName(HttpServletRequest request) {
                    return request.getParameter("username");
                }

                @Override
                public String getPassword(HttpServletRequest request) {
                    return request.getParameter("password");
                }

                @Override
                public String getAreaCode(HttpServletRequest request) {
                    return request.getParameter("areaCode");
                }

                @Override
                public String getPhoneNumber(HttpServletRequest request) {
                    return request.getParameter("phoneNumber");
                }

                @Override
                public String getEmail(HttpServletRequest request) {
                    return request.getParameter("email");
                }

                @Override
                public String getVerifyCode(HttpServletRequest request) {
                    return request.getParameter("verifyCode");
                }
            });
            
            http.apply(loginConfigurer);
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }

    public ITokenStorage getTokenStorage() {
        return this.tokenStorage;
    }
    
}
