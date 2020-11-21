package com.soonsoft.uranus.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.paging.PagingInterceptor;
import com.soonsoft.uranus.data.paging.postgresql.PostgreSQLPagingDailect;
import com.soonsoft.uranus.services.membership.MembershipConfiguration;
import com.soonsoft.uranus.api.config.properties.MasterDataSourceProperties;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@AutoConfigureBefore({ WebMvcAutoConfiguration.class })
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

    @Bean(name = "masterTransactionManager")
    public DataSourceTransactionManager sentinelTransactionManager(@Qualifier("master") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "securityAccess")
    @Primary
    public IDatabaseAccess membershipAccess(
        @Qualifier("master") DataSource dataSource, 
        org.apache.ibatis.session.Configuration mybatisConfig) throws Exception {
        MembershipConfiguration config = new MembershipConfiguration();
        return config.createDatabaseAccess(dataSource, mybatisConfig);
    }
}
