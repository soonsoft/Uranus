package com.soonsoft.uranus.security.config.api.configurer;

import com.soonsoft.uranus.security.config.ICustomConfigurer;
import com.soonsoft.uranus.security.config.SecurityConfigException;
import com.soonsoft.uranus.security.config.api.WebApiLoginConfigurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class LoginConfigurer implements ICustomConfigurer {

    private String sessionIdHeaderName;

    public LoginConfigurer() {

    }

    public LoginConfigurer(String sessionIdHeaderName) {
        this.sessionIdHeaderName = sessionIdHeaderName;
    }

    @Override
    public void config(HttpSecurity http) {
        try {
            http.apply(new WebApiLoginConfigurer<>(sessionIdHeaderName));
        } catch (Exception e) {
            throw new SecurityConfigException("apply WebApiLoginConfigurer error.", e);
        }
    }
    
}
