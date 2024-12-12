package com.soonsoft.uranus.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface ICustomConfigurer {

    void config(HttpSecurity http, WebApplicationSecurityConfig config);
    
}
