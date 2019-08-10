package com.soonsoft.uranus.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * ISecurityConfig
 */
public interface ISecurityConfig {

    void config(WebSecurity web);

    void config(HttpSecurity http);
    
}