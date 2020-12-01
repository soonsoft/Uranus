package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class WebApiLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, WebApiLoginConfigurer<H>, WebApiUsernamePasswordAuthenticationFilter> {

    public static final String SECURITY_FORM_USERNAME_NAME = "username";
    public static final String SECURITY_FORM_PASSWORD_NAME = "password";

    private static final String DEFAULT_LOGIN_PROCESSING_URL = "/account/login";

    public WebApiLoginConfigurer(String sessionIdHeaderName) {
        this(
            DEFAULT_LOGIN_PROCESSING_URL, 
            new WebApiUsernamePasswordAuthenticationFilter(),
            new WebApiAuthenticationSuccessHandler(sessionIdHeaderName), 
            new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(String sessionIdHeaderName, String loginProcessingUrl, IUsernamePasswordGetter getter) {
        this(loginProcessingUrl, getter, new WebApiAuthenticationSuccessHandler(sessionIdHeaderName), new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(
        String loginProcessingUrl, 
        IUsernamePasswordGetter getter, 
        AuthenticationSuccessHandler successHandler, 
        AuthenticationFailureHandler failureHandler) {

        this(
            loginProcessingUrl, 
            new WebApiUsernamePasswordAuthenticationFilter(getter), 
            successHandler, failureHandler);
    }

    protected WebApiLoginConfigurer(
        String loginProcessingUrl, 
        WebApiUsernamePasswordAuthenticationFilter authenticationFilter, 
        AuthenticationSuccessHandler successHandler, 
        AuthenticationFailureHandler failureHandler) {

        super(authenticationFilter, loginProcessingUrl);
        successHandler(successHandler);
        failureHandler(failureHandler);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    // 登录成功处理器
    private static class WebApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        private String sessionIdHeader;

        public WebApiAuthenticationSuccessHandler(String sessionIdHeader) {
            this.sessionIdHeader = sessionIdHeader;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            if(!StringUtils.isEmpty(sessionIdHeader)) {
                response.setHeader(sessionIdHeader, request.getSession().getId());
            }

            final Integer statusCode = HttpStatus.OK.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, authentication));
        }

    }

    // 登录失败处理器
    private static class WebApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, "Incorrect username or password."));
        }

    }
    
}