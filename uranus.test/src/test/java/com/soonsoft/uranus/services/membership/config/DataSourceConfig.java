package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.config.DataSourceInitializer;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@SpringBootConfiguration
@Import(value = MembershipDataSourceProperty.class)
public class DataSourceConfig {

    @Autowired
    private MembershipDataSourceProperty membershipDataSourceProperty;

    @Bean(name = "membership")
    public DataSource getDataSource() {
        HikariDataSource dataSource =
            DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(membershipDataSourceProperty.getUrl())
                .driverClassName(membershipDataSourceProperty.getDriverClassName())
                .username(membershipDataSourceProperty.getUsername())
                .password(membershipDataSourceProperty.getPassword())
                .build();

        DataSourceInitializer.init(dataSource, membershipDataSourceProperty);

        
        return dataSource;
    }

}