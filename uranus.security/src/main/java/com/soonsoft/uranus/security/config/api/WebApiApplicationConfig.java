package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationConfig;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class WebApiApplicationConfig extends WebApplicationConfig {

    @Override
    public void config(WebSecurity web) {
    }

    @Override
    public void config(HttpSecurity http) {
        try {
            http.requestMatchers()
                    .antMatchers("/**")
                .and()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .withObjectPostProcessor(getPostProcessor())
                .and()
                    .cors()
                .and()
                    .csrf().disable()
                .formLogin(formLogin -> {
                    formLogin.usernameParameter("username");
                    formLogin.passwordParameter("password");
                    formLogin.successHandler(new WebApiAuthenticationSuccessHandler());
                    formLogin.failureHandler(new WebApiAuthenticationFailureHandler());
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout").permitAll();
                    logout.addLogoutHandler(null);
                })
                .exceptionHandling()
                    .authenticationEntryPoint(new WebApiAuthenticationEntryPoint())
                    .accessDeniedHandler(new WebApiAccessDeniedHandler());

            //http.apply(new WebApiFormLoginConfigurer<>());
        } catch (Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }

    private static final class WebApiFormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
            AbstractAuthenticationFilterConfigurer<H, WebApiFormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter> {

        public WebApiFormLoginConfigurer() {
            super(new WebApiUsernamePasswordAuthenticationFilter(), null);
        }

        @Override
        protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
            return new AntPathRequestMatcher(loginProcessingUrl, "POST");
        }

    }

    private static class WebApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.UNAUTHORIZED.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, "Unauthorized"));
		}

    }

    private static class WebApiAccessDeniedHandler implements AccessDeniedHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.FORBIDDEN.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, "Forbidden"));
		}
        
    }

    // 登录成功处理器
    private static class WebApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.OK.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, authentication));
            //request.getSession().getId()
        }

    }

    // 登录失败处理器
    private static class WebApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException exception) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.setStatus(statusCode);
            response.getWriter().print(new SecurityResult(statusCode, exception.getMessage()));
        }

    }
    
}
