package com.soonsoft.uranus.site.config;

import com.soonsoft.uranus.data.config.DataSourceInitializer;
import com.soonsoft.uranus.site.config.properties.MasterDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore({ WebMvcAutoConfiguration.class })
public class DataSourceConfiguration {

    @Resource
    private MasterDataSourceProperties masterDataSourceProperties;

    @Bean(name = "master")
    @Primary
    public DataSource masterDataSource() {
        HikariDataSource dataSource = 
            DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(masterDataSourceProperties.getUrl())
                .driverClassName(masterDataSourceProperties.getDriverClassName())
                .username(masterDataSourceProperties.getUsername())
                .password(masterDataSourceProperties.getPassword())
                .build();

        DataSourceInitializer.init(dataSource, masterDataSourceProperties);
    
        return dataSource;
    }

}
