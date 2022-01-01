package com.soonsoft.uranus.services.membership.config;

import javax.annotation.Resource;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.security.config.BaseSecurityConfiguration;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.services.membership.config.properties.MembershipProperties;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable
public class MembershipConfiguration extends BaseSecurityConfiguration {

    @Resource
    private MembershipProperties membershipProperties;

    @Override
    public SecurityProperties getSecurityProperties() {
        return membershipProperties;
    }

    @Bean
    public UserServiceFactory userServiceFactory(PasswordEncoder passwordEncoder) {
        String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new UserServiceFactory(applicationContext.getBean(IDatabaseAccess.class), passwordEncoder);
        }

        return new UserServiceFactory(applicationContext, membershipDatabaseAccessName, passwordEncoder);
    }

    @Bean
    public RoleServiceFactory roleServiceFactory() {
        String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new RoleServiceFactory(applicationContext.getBean(IDatabaseAccess.class));
        }

        return new RoleServiceFactory(applicationContext, membershipDatabaseAccessName);
    }

    @Bean
    public FunctionServiceFactory functionServiceFactory() {
    String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new FunctionServiceFactory(applicationContext.getBean(IDatabaseAccess.class));
        }

        return new FunctionServiceFactory(applicationContext, membershipDatabaseAccessName);
    }

}