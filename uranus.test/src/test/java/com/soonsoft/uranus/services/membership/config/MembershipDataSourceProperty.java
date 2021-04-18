package com.soonsoft.uranus.services.membership.config;

import com.soonsoft.uranus.data.config.properties.HikariDataSourceProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.datasource.membership")
public class MembershipDataSourceProperty extends HikariDataSourceProperty {
    
}
