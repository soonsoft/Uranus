package com.soonsoft.uranus.security.config;

import com.soonsoft.uranus.security.authorization.WebAccessDecisionManager;
import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * WebApplicationConfig
 */
public class WebApplicationConfig implements ISecurityConfig {

    private WebAccessDecisionManager webAccessDecisionManager;

    private WebSecurityMetadataSource webSecurityMetadataSource;

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
                    //.logoutSuccessHandler() //退出成功 
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