package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.authentication.ITokenProvider;
import com.soonsoft.uranus.security.authentication.ITokenStrategy;
import com.soonsoft.uranus.security.authentication.WebAuthenticationFilter;
import com.soonsoft.uranus.security.authentication.jwt.JWTAuthenticationToken;
import com.soonsoft.uranus.security.config.WebLoginConfigurer;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class WebApiLoginConfigurer extends WebLoginConfigurer<HttpSecurity> {

    public WebApiLoginConfigurer(ITokenProvider<?> tokenProvider) {
        super(new WebAuthenticationFilter(tokenProvider), null, null);
        successHandler(new WebApiAuthenticationSuccessHandler(tokenProvider.getTokenStrategy(), this));
        failureHandler(new WebApiAuthenticationFailureHandler(this));
    }

    public void setLoginTokenRefreshUrl(String url) {
        getAuthenticationFilter().setLoginTokenRefreshUrl(url);
    }

    // 登录成功处理器
    private static class WebApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        private ITokenStrategy<?> tokenStrategy;
        private final WebApiLoginConfigurer configurer;

        public WebApiAuthenticationSuccessHandler(ITokenStrategy<?> tokenStrategy, WebApiLoginConfigurer configurer) {
            this.tokenStrategy = tokenStrategy;
            this.configurer = configurer;
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

            configurer.onSuccess(request, response);
        }

    }

    // 登录失败处理器
    private static class WebApiAuthenticationFailureHandler implements AuthenticationFailureHandler {
        private final WebApiLoginConfigurer configurer;

        public WebApiAuthenticationFailureHandler(WebApiLoginConfigurer configurer) {
            this.configurer = configurer;
        }

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

            configurer.onFailed(request, response);
        }

    }
    
}
