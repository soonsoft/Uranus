package com.soonsoft.uranus.site.config.properties;

import com.soonsoft.uranus.data.config.properties.HikariDataSourceProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.datasource.master")
public class MasterDataSourceProperties extends HikariDataSourceProperty {

}
