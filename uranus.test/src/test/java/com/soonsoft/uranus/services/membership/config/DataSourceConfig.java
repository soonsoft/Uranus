package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


@SpringBootConfiguration
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    @Bean(name = "membership")
    public DataSource getDataSource() {
        DruidDataSource dataSource =
                DataSourceBuilder
                        .create()
                        .type(DruidDataSource.class)
                        .url(environment.getProperty("jdbc.membership.url"))
                        .driverClassName(environment.getProperty("jdbc.membership.driver"))
                        .username(environment.getProperty("jdbc.membership.username"))
                        .password(environment.getProperty("jdbc.membership.password"))
                        .build();

        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(1);
        dataSource.setMaxWait(5000);
        // 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
        // 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        return dataSource;
    }

}