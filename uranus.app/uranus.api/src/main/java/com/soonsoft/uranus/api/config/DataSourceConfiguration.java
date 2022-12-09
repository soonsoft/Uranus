package com.soonsoft.uranus.api.config;

import com.soonsoft.uranus.api.config.properties.MasterDataSourceProperties;
import com.soonsoft.uranus.data.config.DataSourceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    private final MasterDataSourceProperties masterDataSourceProperties;

    public DataSourceConfiguration(MasterDataSourceProperties masterDataSourceProperties) {
        this.masterDataSourceProperties = masterDataSourceProperties;
    }

    @Bean(name = "master")
    @Primary
    public DataSource masterDataSource() {
        return DataSourceFactory.create(masterDataSourceProperties);
    }

}
