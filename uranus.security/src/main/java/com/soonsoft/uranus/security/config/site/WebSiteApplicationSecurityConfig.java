package com.soonsoft.uranus.security.config.site;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authentication.ILoginParameterGetter;
import com.soonsoft.uranus.security.authentication.UserLoginFunction;
import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfig;
import com.soonsoft.uranus.security.config.constant.SecurityConfigUrlConstant;

import javax.servlet.http.HttpServletRequest;

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
            // .formLogin(
            //     login -> login
            //         .loginPage(SecurityConfigUrlConstant.LoginPasswordUrl)
            //         .usernameParameter("username")
            //         .passwordParameter("password")
            //         .successHandler(null)
            //         .failureHandler(null)
            //         .defaultSuccessUrl("/").permitAll()
            //         .failureUrl(SecurityConfigUrlConstant.LoginPasswordUrl + "?error").permitAll()
            // )
            .logout(
                logout -> logout
                    .logoutUrl(SecurityConfigUrlConstant.LogoutUrl)
                    .permitAll()
            );

            // 这里必须先调用apply方法，否则会报错
            WebSiteLoginConfigurer loginConfigurer = 
                http.apply(
                    new WebSiteLoginConfigurer(
                        SecurityConfigUrlConstant.SiteLoginSuccessUrl, 
                        StringUtils.format("{0}?error", SecurityConfigUrlConstant.LoginPasswordUrl))
                );

            loginConfigurer.setLoginPage(SecurityConfigUrlConstant.SiteLoginPage);
            loginConfigurer.setLoginPasswordUrl(SecurityConfigUrlConstant.LoginPasswordUrl);
            loginConfigurer.setLoginPasswordFn(getUserLoginFunction().getLoginPasswordFn());

            loginConfigurer.setLoginParameterGetter(new ILoginParameterGetter() {
                @Override
                public String getUserName(HttpServletRequest request) {
                    return request.getParameter("username");
                }

                @Override
                public String getPassword(HttpServletRequest request) {
                    return request.getParameter("password");
                }
            });
        } catch(Exception e) {
            throw new SecurityConfigException("WebApplicationConfig error.", e);
        }
    }
    
}
