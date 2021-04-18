package com.soonsoft.uranus.api.config.properties;

import com.soonsoft.uranus.data.config.properties.HikariDataSourceProperty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.datasoure.master")
public class MasterDataSourceProperties extends HikariDataSourceProperty {

}
