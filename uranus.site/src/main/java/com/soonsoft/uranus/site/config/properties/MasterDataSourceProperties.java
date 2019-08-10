package com.soonsoft.uranus.site.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("classpath:jdbc.properties")
public class MasterDataSourceProperties {

    @Value("${jdbc.master.driver}")
    private String driverClassName;

    @Value("${jdbc.master.url}")
    private String url;

    @Value("${jdbc.master.username}")
    private String username;

    @Value("${jdbc.master.password}")
    private String password;

    @Value("${jdbc.master.initialSize:1}")
    private Integer initialSize = 1;

    @Value("${jdbc.master.minIdle:1}")
    private Integer minIdle = 1;

    @Value("${jdbc.master.maxActive:20}")
    private Integer maxActive = 20;

    @Value("${jdbc.master.maxWait:60000}")
    private Integer maxWait = 60000;

    @Value("${jdbc.master.timeBetweenEvictionRunsMillis:60000}")
    private Integer timeBetweenEvictionRunsMillis = 60000;

    @Value("${jdbc.master.minEvictableIdleTimeMillis:300000}")
    private Integer minEvictableIdleTimeMillis = 300000;

    @Value("${jdbc.master.maxPoolPreparedStatementPerConnectionSize:20}")
    private Integer maxPoolPreparedStatementPerConnectionSize = 20;

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }
}
