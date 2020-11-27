package com.soonsoft.uranus.security.config.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationConfig;
import com.soonsoft.uranus.security.jwt.JWTHttpSessionSecurityContextRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

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
                .apply(new WebApiLoginConfigurer<>())
                .and()
                    .logout(logout -> {
                        logout.logoutUrl("/logout").permitAll();
                    })
                .exceptionHandling()
                    .authenticationEntryPoint(new WebApiAuthenticationEntryPoint())
                    .accessDeniedHandler(new WebApiAccessDeniedHandler());
            http.addFilterAt(
                new SecurityContextPersistenceFilter(new JWTHttpSessionSecurityContextRepository()), 
                SecurityContextPersistenceFilter.class);
        } catch (Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
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
    
}
