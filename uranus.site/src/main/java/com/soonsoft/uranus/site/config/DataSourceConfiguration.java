package com.soonsoft.uranus.site.config;

import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.site.config.properties.MasterDataSourceProperties;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
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
        return DataSourceFactory.create(masterDataSourceProperties);
    }

}
