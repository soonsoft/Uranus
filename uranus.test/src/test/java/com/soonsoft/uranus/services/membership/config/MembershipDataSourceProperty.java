package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.config.properties.HikariDataSourceProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.datasource.membership")
public class MembershipDataSourceProperty extends HikariDataSourceProperty {

    public MembershipDataSourceProperty() {
        this.setDriverClassName("org.postgresql.Driver");
        this.setUrl("jdbc:postgresql://localhost:5432/postgres");
        this.setUsername("postgres");
        this.setPassword("soon0116");
        this.setPoolName("membership-pool");
    }
    
}
