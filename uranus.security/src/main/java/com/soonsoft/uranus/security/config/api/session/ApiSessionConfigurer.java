package com.soonsoft.uranus.security.config.api.session;

import com.soonsoft.uranus.security.authentication.ITokenProvider;
import com.soonsoft.uranus.security.authentication.session.ApiSessionTokenProvider;
import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.api.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;
import com.soonsoft.uranus.security.config.api.WebApiSecurityContextHolderFilter;
import com.soonsoft.uranus.security.config.api.WebApiSecurityContextHolderFilter.WebApiHttpSessionSecurityContextRepository;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * API 接口适配Session模式，适合前后端分离的多页应用
 */
public class ApiSessionConfigurer implements ICustomConfigurer {

    private final static String DEFAULT_SESSION_ID_HEADER_NAME = "URANUS-AUTH_SID";

    private String sessionIdHeaderName;
    private IRealHttpServletRequestHook requestHook;
    private SecurityContextRepository securityContextRepository;

    public ApiSessionConfigurer(IRealHttpServletRequestHook requestHook) {
        this(DEFAULT_SESSION_ID_HEADER_NAME, requestHook);
    }

    public ApiSessionConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook) {
        this(sessionIdHeaderName, requestHook, null);
    }

    public ApiSessionConfigurer(String sessionIdHeaderName, IRealHttpServletRequestHook requestHook, SecurityContextRepository repo) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.requestHook = requestHook;
        this.securityContextRepository = repo;
    }

    @Override
    public void config(HttpSecurity http, WebApplicationSecurityConfig config) {
        ITokenProvider<?> tokenProvider = new ApiSessionTokenProvider(sessionIdHeaderName, requestHook);
        if(securityContextRepository == null) {
            securityContextRepository = new WebApiHttpSessionSecurityContextRepository(tokenProvider);
        }

        http.addFilterAt(
                new WebApiSecurityContextHolderFilter(securityContextRepository), 
                SecurityContextHolderFilter.class);

        try {
            WebApiLoginConfigurer loginConfigurer = new WebApiLoginConfigurer(tokenProvider);

            loginConfigurer.setLoginPasswordUrl(SecurityConfigUrlConstant.LogoutUrl);
            loginConfigurer.setLoginVerifyCodeByCellPhoneUrl(SecurityConfigUrlConstant.LoginVerifyCodeCellPhoneUrl);
            loginConfigurer.setLoginVerifyCodeByEmailUrl(SecurityConfigUrlConstant.LoginVerifyCodeEmailUrl);

            loginConfigurer.setLoginPasswordFn(config.getUserLoginFunction().getLoginPasswordFn());
            loginConfigurer.setLoginCellPhoneVerifyCodeFn(config.getUserLoginFunction().getLoginCellPhoneVerifyCodeFn());
            loginConfigurer.setLoginEmailVerifyCodeFn(config.getUserLoginFunction().getLoginEmailVerifyCodeFn());
            
            http.apply(loginConfigurer);
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }

    public String getSessionIdHeaderName() {
        return sessionIdHeaderName;
    }
    
}
