package com.soonsoft.uranus.security.config;

import java.io.IOException;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.core.event.IEvent;
import com.soonsoft.uranus.core.event.SimpleEvent;
import com.soonsoft.uranus.security.authorization.WebAccessDecisionManager;
import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;
import com.soonsoft.uranus.security.event.LogoutEvent;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * WebApplicationConfig
 */
public class WebApplicationConfig implements ISecurityConfig {

    private WebAccessDecisionManager webAccessDecisionManager;

    private WebSecurityMetadataSource webSecurityMetadataSource;

    /** 退出登录成功事件 */
    private IEvent<LogoutEvent> logoutEvent = new SimpleEvent<>("logout");

    private static class WebSecurityLogoutSuccessHandler implements LogoutSuccessHandler {

        private WebApplicationConfig config;

        private WebSecurityLogoutSuccessHandler(WebApplicationConfig config) {
            this.config = config;
        }

        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) throws IOException, ServletException {
            config.logoutEvent.trigger(new LogoutEvent(request, response, authentication));
        }
        
    }

    // 替换FilterSecurityInterceptor中的AccessDecisionManager和SecurityMetadataSource
    private class FilterSecurityInterceptorPostProcessor implements ObjectPostProcessor<FilterSecurityInterceptor> {

        @Override
        public <O extends FilterSecurityInterceptor> O postProcess(O filterSecurityInterceptor) {
            filterSecurityInterceptor.setAccessDecisionManager(webAccessDecisionManager);
            webSecurityMetadataSource.setDefaultSecurityMetadataSource(filterSecurityInterceptor.getSecurityMetadataSource());
            filterSecurityInterceptor.setSecurityMetadataSource(webSecurityMetadataSource);
            return filterSecurityInterceptor;
        }
        
    }

    /** 添加退出登录事件处理函数 */
    public void onLogout(Consumer<LogoutEvent> eventHandler) {
        logoutEvent.on(eventHandler);
    }

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
                    .withObjectPostProcessor(new FilterSecurityInterceptorPostProcessor())
                .and()
                    .csrf().disable()
                .formLogin()
                .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/").permitAll()
                .failureUrl("/login?error").permitAll()
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(new WebSecurityLogoutSuccessHandler(this)) //退出成功
                    .permitAll();
        } catch(Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }

    //#region getter and setter

    public WebAccessDecisionManager getWebAccessDecisionManager() {
        return webAccessDecisionManager;
    }

    public void setWebAccessDecisionManager(WebAccessDecisionManager webAccessDecisionManager) {
        this.webAccessDecisionManager = webAccessDecisionManager;
    }

    public WebSecurityMetadataSource getWebSecurityMetadataSource() {
        return webSecurityMetadataSource;
    }

    public void setWebSecurityMetadataSource(WebSecurityMetadataSource webSecurityMetadataSource) {
        this.webSecurityMetadataSource = webSecurityMetadataSource;
    }

    //#endregion
}