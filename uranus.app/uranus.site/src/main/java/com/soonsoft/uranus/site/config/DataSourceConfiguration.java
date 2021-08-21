package com.soonsoft.uranus.site.config;

import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.site.config.properties.MasterDataSourceProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    private final MasterDataSourceProperties masterDataSourceProperties;

    @Autowired
    public DataSourceConfiguration(MasterDataSourceProperties masterDataSourceProperties) {
        this.masterDataSourceProperties = masterDataSourceProperties;
    }

    @Bean(name = "master")
    @Primary
    public DataSource masterDataSource() {
        return DataSourceFactory.create(masterDataSourceProperties);
    }

}
