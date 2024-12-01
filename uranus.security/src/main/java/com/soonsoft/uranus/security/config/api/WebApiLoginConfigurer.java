package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.authentication.IRefreshTokenGetter;
import com.soonsoft.uranus.security.authentication.ITokenProvider;
import com.soonsoft.uranus.security.authentication.ITokenStrategy;
import com.soonsoft.uranus.security.authentication.jwt.JWTAuthenticationToken;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
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

    public WebApiLoginConfigurer(ITokenProvider<?> tokenProvider, String loginProcessUrl) {
        this(
            loginProcessUrl, 
            new WebApiUsernamePasswordAuthenticationFilter(tokenProvider),
            new WebApiAuthenticationSuccessHandler(tokenProvider.getTokenStrategy()), 
            new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(ITokenProvider<?> tokenProvider, String loginProcessUrl, String refreshProcessUrl) {
        this(
            loginProcessUrl, 
            new WebApiUsernamePasswordAuthenticationFilter(tokenProvider, refreshProcessUrl),
            new WebApiAuthenticationSuccessHandler(tokenProvider.getTokenStrategy()), 
            new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(
        ITokenProvider<?> tokenProvider, 
        String loginProcessingUrl, IUsernamePasswordGetter usernamePasswordGetter) {
        
        this(
            tokenProvider, 
            loginProcessingUrl, usernamePasswordGetter, 
            null, null, 
            new WebApiAuthenticationSuccessHandler(tokenProvider.getTokenStrategy()), new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(
        ITokenProvider<?> tokenProvider,
        String loginProcessingUrl, IUsernamePasswordGetter usernamePasswordGetter,
        String refreshProcessingUrl, IRefreshTokenGetter refreshTokenGetter) {

        this(
            tokenProvider, 
            loginProcessingUrl, usernamePasswordGetter, 
            refreshProcessingUrl, refreshTokenGetter, 
            new WebApiAuthenticationSuccessHandler(tokenProvider.getTokenStrategy()), new WebApiAuthenticationFailureHandler());
    }

    public WebApiLoginConfigurer(
        ITokenProvider<?> tokenProvider ,
        String loginProcessingUrl,
        IUsernamePasswordGetter usernamePasswordGetter, 
        String refreshProcessingUrl, 
        IRefreshTokenGetter refreshTokenGetter,
        AuthenticationSuccessHandler successHandler, 
        AuthenticationFailureHandler failureHandler) {

        this(
            loginProcessingUrl, 
            new WebApiUsernamePasswordAuthenticationFilter(tokenProvider, usernamePasswordGetter, refreshProcessingUrl, refreshTokenGetter), 
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
        private ITokenStrategy<?> tokenStrategy;

        public WebApiAuthenticationSuccessHandler(ITokenStrategy<?> tokenStrategy) {
            this.tokenStrategy = tokenStrategy;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.OK.value();
            response.setStatus(statusCode);
            SecurityResult securityResult = new SecurityResult(statusCode, authentication);
            
            Object token = tokenStrategy.getToken(request, response, authentication);
            if(token instanceof String) {
                securityResult.setAccessToken((String) token);
            } else {
                JWTAuthenticationToken jwtToken = (JWTAuthenticationToken) token;
                securityResult.setAccessToken(jwtToken.getAccessToken());

                String refreshToken = jwtToken.getRefreshToken();
                securityResult.setRefreshToken(refreshToken);
            }
            response.getWriter().print(securityResult);
        }

    }

    // 登录失败处理器
    private static class WebApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
                
            Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            String message = "Incorrect username or password.";
            
            if(exception instanceof BadCredentialsException) {
                statusCode = HttpStatus.UNAUTHORIZED.value();
                message = exception.getMessage();
            }

            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, message));
        }

    }
    
}
