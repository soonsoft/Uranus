package com.soonsoft.uranus.security.config.site;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

public class WebSiteApplicationSecurityConfig extends WebApplicationSecurityConfig {

    public WebSiteApplicationSecurityConfig(ICustomConfigurer... configurers) {
        setConfigurerList(configurers);
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
                    .withObjectPostProcessor(getPostProcessor())
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
                    .logoutUrl("/logout").permitAll();
        } catch(Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }
    
}
