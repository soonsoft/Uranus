package com.soonsoft.uranus.security.config.site;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;

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
            http.authorizeHttpRequests(
                authorize -> authorize
                    .anyRequest().authenticated()
                    .anyRequest().access(getWebAuthorizationManager())
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(
                login -> login
                    .loginPage(SecurityConfigUrlConstant.SiteLoginUrl)
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/").permitAll()
                    .failureUrl(SecurityConfigUrlConstant.SiteLoginUrl + "?error").permitAll()
            )
            .logout(
                logout -> logout
                    .logoutUrl(SecurityConfigUrlConstant.SiteLogoutUrl)
                    .permitAll()
            );
        } catch(Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }
    
}
