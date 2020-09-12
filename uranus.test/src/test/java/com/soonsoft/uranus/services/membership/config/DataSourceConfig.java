package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.paging.PagingInterceptor;
import com.soonsoft.uranus.data.paging.postgresql.PostgreSQLPagingDailect;
import com.soonsoft.uranus.services.membership.MembershipConfiguration;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * DataSourceConfig
 */
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

    @Bean
    public org.apache.ibatis.session.Configuration mybatisConfiguration() {
        // http://www.mybatis.org/mybatis-3/zh/configuration.html
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setCacheEnabled(true);
        config.setLogPrefix("[Mybatis-SQL]");
        config.setLogImpl(Slf4jImpl.class);
        config.addInterceptor(new PagingInterceptor(new PostgreSQLPagingDailect()));
        return config;
    }

    @Bean(name = "membershipTransactionManager")
    public DataSourceTransactionManager sentinelTransactionManager(@Qualifier("membership") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "securityAccess")
    @Primary
    public IDatabaseAccess membershipAccess(
        @Qualifier("membership") DataSource dataSource, 
        org.apache.ibatis.session.Configuration mybatisConfig) throws Exception {

        MembershipConfiguration config = new MembershipConfiguration();
        return config.createDatabaseAccess(dataSource, mybatisConfig);
    }

}