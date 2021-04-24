package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.config.DataSourceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@SpringBootConfiguration
@Import(value = MembershipDataSourceProperty.class)
public class DataSourceConfig {

    @Autowired
    private MembershipDataSourceProperty membershipDataSourceProperty;

    @Bean(name = "membership")
    public DataSource getDataSource() {
        return DataSourceFactory.create(membershipDataSourceProperty);
    }

}