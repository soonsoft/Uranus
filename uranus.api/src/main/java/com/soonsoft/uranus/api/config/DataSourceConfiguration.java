package com.soonsoft.uranus.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.soonsoft.uranus.api.config.properties.MasterDataSourceProperties;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Resource
    private MasterDataSourceProperties masterDataSourceProperties;

    @Bean(name = "master")
    @Primary
    public DataSource masterDataSource() {
        DruidDataSource dataSource = DataSourceBuilder.create().type(DruidDataSource.class)
                .url(masterDataSourceProperties.getUrl()).driverClassName(masterDataSourceProperties.getDriverClassName())
                .username(masterDataSourceProperties.getUsername()).password(masterDataSourceProperties.getPassword()).build();

        // 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        dataSource.setInitialSize(masterDataSourceProperties.getInitialSize());
        // 最小连接池数量
        dataSource.setMinIdle(masterDataSourceProperties.getMinIdle());
        // 最大连接池数量
        dataSource.setMaxActive(masterDataSourceProperties.getMaxActive());
        // 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
        dataSource.setMaxWait(masterDataSourceProperties.getMaxWait());
        // 如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接
        dataSource.setTimeBetweenEvictionRunsMillis(masterDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        // 连接保持空闲而不被驱逐的最小时间
        dataSource.setMinEvictableIdleTimeMillis(masterDataSourceProperties.getMinEvictableIdleTimeMillis());
        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                masterDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());

        // 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
        // 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        // 连接泄漏处理。Druid提供了RemoveAbandanded相关配置，用来关闭长时间不使用的连接
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        // 关闭abanded连接时输出错误日志
        dataSource.setLogAbandoned(true);

        return dataSource;
    }

}
