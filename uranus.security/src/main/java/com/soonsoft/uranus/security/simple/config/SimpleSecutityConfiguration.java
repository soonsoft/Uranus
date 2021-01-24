package com.soonsoft.uranus.security.simple.config;

import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.BaseSecurityConfiguration;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.simple.service.SimpleFunctionManager;
import com.soonsoft.uranus.security.simple.service.SimpleRoleManager;
import com.soonsoft.uranus.security.simple.service.SimpleUserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SimpleSecutityConfiguration extends BaseSecurityConfiguration {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Bean(name = "userManager")
    public IUserManager userManager(PasswordEncoder passwordEncoder) {
        return new SimpleUserManager(passwordEncoder);
    }

    @Bean(name = "roleManager")
    public IRoleManager roleManager() {
        return new SimpleRoleManager();
    }

    @Bean(name = "functionManager")
    public IFunctionManager functionManager() {
        return new SimpleFunctionManager();
    }
    
}
