package com.soonsoft.uranus.security.config.site;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authentication.UserLoginFunction;
import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

public class WebSiteApplicationSecurityConfig extends WebApplicationSecurityConfig {

    public WebSiteApplicationSecurityConfig(UserLoginFunction userLoginFunction, ICustomConfigurer... configurers) {
        setUserLoginFunction(userLoginFunction);
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
                    .requestMatchers(getPermitPatterns()).permitAll() // 设置不做鉴权的url 
                    .anyRequest().access(getWebAuthorizationManager())
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(
                login -> login
                    .loginPage(SecurityConfigUrlConstant.SiteLoginUrl)
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(null)
                    .failureHandler(null)
                    .defaultSuccessUrl("/").permitAll()
                    .failureUrl(SecurityConfigUrlConstant.SiteLoginUrl + "?error").permitAll()
            )
            .logout(
                logout -> logout
                    .logoutUrl(SecurityConfigUrlConstant.SiteLogoutUrl)
                    .permitAll()
            );

            WebSiteLoginConfigurer loginConfigurer = 
                new WebSiteLoginConfigurer(
                    SecurityConfigUrlConstant.SiteLoginSuccessUrl, 
                    StringUtils.format("{0}?error", SecurityConfigUrlConstant.LoginPasswordUrl));

            loginConfigurer.setLoginPasswordUrl(SecurityConfigUrlConstant.LoginPasswordUrl);
            loginConfigurer.setLoginPasswordFn(getUserLoginFunction().getLoginPasswordFn());

            http.apply(loginConfigurer);
        } catch(Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }
    
}
