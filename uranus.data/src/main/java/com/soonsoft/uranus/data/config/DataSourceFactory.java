package com.soonsoft.uranus.data.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.soonsoft.uranus.core.common.lang.TypeUtils;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.data.config.properties.DataSourceProperty;
import com.soonsoft.uranus.data.config.properties.DruidDataSourceProperty;
import com.soonsoft.uranus.data.config.properties.HikariDataSourceProperty;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceFactory {

    private final static Map<String, Action2<DataSource, DataSourceProperty>> datasourceSettingsMap = new HashMap<>();

    static {
        datasourceSettingsMap.put("com.alibaba.druid.pool.DruidDataSource", DataSourceFactory::initDruidDataSource);
        datasourceSettingsMap.put("com.zaxxer.hikari.HikariDataSource", DataSourceFactory::initHikariDataSource);
    }

    public static DataSource create(DataSourceProperty dataSourceProperty) {
        if(dataSourceProperty == null) {
            throw new IllegalArgumentException("the parameter dataSourceProperty is required.");
        }

        DataSource dataSource = TypeUtils.createInstance(dataSourceProperty.getDataSourceType());
        Action2<DataSource, DataSourceProperty> initAction = datasourceSettingsMap.get(dataSource.getClass().getName());
        if(initAction != null) {
            initAction.apply(dataSource, dataSourceProperty);
        }

        return dataSource;
    }

    private static void initDruidDataSource(DataSource dataSource, DataSourceProperty dataSourceProperty) {
        DruidDataSourceProperty druidDataSourceProperty = (DruidDataSourceProperty) dataSourceProperty;
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        druidDataSource.setUrl(dataSourceProperty.getUrl());
        druidDataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
        druidDataSource.setUsername(dataSourceProperty.getUsername());
        druidDataSource.setPassword(dataSourceProperty.getPassword());
        // 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        druidDataSource.setInitialSize(druidDataSourceProperty.getInitialSize());
        // 最小连接池数量
        druidDataSource.setMinIdle(druidDataSourceProperty.getMinIdle());
        // 最大连接池数量
        druidDataSource.setMaxActive(druidDataSourceProperty.getMaxActive());
        // 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
        druidDataSource.setMaxWait(druidDataSourceProperty.getMaxWait());
        // 如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidDataSourceProperty.getTimeBetweenEvictionRunsMillis());
        // 连接保持空闲而不被驱逐的最小时间
        druidDataSource.setMinEvictableIdleTimeMillis(druidDataSourceProperty.getMinEvictableIdleTimeMillis());
        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(
                druidDataSourceProperty.getMaxPoolPreparedStatementPerConnectionSize());

        // 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
        // 如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        // 连接泄漏处理。Druid提供了RemoveAbandanded相关配置，用来关闭长时间不使用的连接
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1800);
        // 关闭abanded连接时输出错误日志
        druidDataSource.setLogAbandoned(true);
    }

    private static void initHikariDataSource(DataSource dataSource, DataSourceProperty dataSourceProperty) {
        HikariDataSourceProperty hikariDataSourceProperty = (HikariDataSourceProperty) dataSourceProperty;
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

        hikariDataSource.setJdbcUrl(dataSourceProperty.getUrl());
        hikariDataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
        hikariDataSource.setUsername(dataSourceProperty.getUsername());
        hikariDataSource.setPassword(dataSourceProperty.getPassword());

        hikariDataSource.setAutoCommit(hikariDataSourceProperty.isAutoCommit());
        hikariDataSource.setConnectionTimeout(hikariDataSourceProperty.getConnectionTimeout());
        hikariDataSource.setIdleTimeout(hikariDataSourceProperty.getIdleTimeout());
        hikariDataSource.setKeepaliveTime(hikariDataSourceProperty.getKeepaliveTime());
        hikariDataSource.setMaxLifetime(hikariDataSourceProperty.getMaxLifetime());
        hikariDataSource.setMinimumIdle(hikariDataSourceProperty.getMinimumIdle());
        hikariDataSource.setMaximumPoolSize(hikariDataSourceProperty.getMaximumPoolSize());
        hikariDataSource.setPoolName(hikariDataSourceProperty.getPoolName());
        hikariDataSource.setReadOnly(hikariDataSourceProperty.isReadonly());
    }
    
}
