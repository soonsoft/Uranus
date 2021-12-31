package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.EnableDatabaseAccess;
import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.data.IDatabaseAccess;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootConfiguration
@Import(value = MembershipDataSourceProperty.class)
@EnableDatabaseAccess(primaryName = "membership", entityClassPackages = "com.soonsoft.uranus.services.membership.po")
public class MembershipConfig {

    @Bean(name = "membership")
    public DataSource dataSource(MembershipDataSourceProperty membershipDataSourceProperty) {
        return DataSourceFactory.create(membershipDataSourceProperty);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserServiceFactory userServiceFactory(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess, PasswordEncoder passwordEncoder) {
        return new UserServiceFactory(securityAccess, passwordEncoder);
    }

    @Bean("membershipRoleService")
    public RoleServiceFactory roleServiceFactory(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess) {
        return new RoleServiceFactory(securityAccess);
    }

    @Bean
    public FunctionServiceFactory functionServiceFactory(@Qualifier("membershipAccess") IDatabaseAccess<?> securityAccess) {
        return new FunctionServiceFactory(securityAccess);
    }

}