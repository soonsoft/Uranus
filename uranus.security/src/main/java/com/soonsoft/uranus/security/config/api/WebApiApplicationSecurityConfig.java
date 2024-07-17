package com.soonsoft.uranus.security.config.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import static org.springframework.security.config.Customizer.withDefaults;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.api.jwt.JWTConfigurer;
import com.soonsoft.uranus.security.config.api.jwt.token.JWTAuthenticationToken;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;
import com.soonsoft.uranus.security.entity.SecurityUser;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class WebApiApplicationSecurityConfig extends WebApplicationSecurityConfig {

    public WebApiApplicationSecurityConfig(ICustomConfigurer... configurers) {
        setConfigurerList(configurers);
    }

    @Override
    public void config(WebSecurity web) {
    }

    @Override
    public void config(HttpSecurity http) {
        final LogoutHandler logoutHandler = getLogoutHandler();
        try {
            http
                .authorizeHttpRequests(
                    authorize -> authorize
                        .requestMatchers(getPermitPatterns()).permitAll() // 设置不做鉴权的url
                        .anyRequest().access(getWebAuthorizationManager()) // 同时包含了身份验证与权限验证
                )
                .cors(withDefaults())
                .csrf(config -> config.disable())
                .logout(logout -> {
                    if (logoutHandler != null) {
                        logout.addLogoutHandler(logoutHandler);
                    }
                    logout.logoutSuccessHandler(new WebApiLogoutSuccessHandler());
                    logout.logoutUrl(SecurityConfigUrlConstant.WebAplLogoutUrl).permitAll();
                })
                .exceptionHandling(
                    handling -> handling
                        .authenticationEntryPoint(new WebApiAuthenticationEntryPoint())
                        .accessDeniedHandler(new WebApiAccessDeniedHandler())
                );

            setConfig(http);
        } catch (Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }

    protected LogoutHandler getLogoutHandler() {
        List<ICustomConfigurer> configurerList = getConfigurerList();
        if(configurerList != null) {
            for(ICustomConfigurer configurer : configurerList) {
                if(configurer instanceof JWTConfigurer) {
                    final ITokenStorage tokenStorage = ((JWTConfigurer) configurer).getTokenStorage();
                    return new LogoutHandler(){
                        @Override
                        public void logout(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) {
                            
                            if(authentication instanceof JWTAuthenticationToken) {
                                JWTAuthenticationToken jwtAuthenticationToken = (JWTAuthenticationToken) authentication;
                                String username = ((SecurityUser) jwtAuthenticationToken.getPrincipal()).getUsername();
                                tokenStorage.remove(username);
                            }

                        }
                        
                    };
                }
            }
        }
        return null;
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

    private static class WebApiLogoutSuccessHandler implements LogoutSuccessHandler {

		@Override
		public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
            final Integer statusCode = HttpStatus.OK.value();
            response.getWriter().print(new SecurityResult(statusCode, "LogOff"));
		}
        
    }
    
}
