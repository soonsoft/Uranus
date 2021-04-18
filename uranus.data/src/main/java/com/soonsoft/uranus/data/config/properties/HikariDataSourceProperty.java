package com.soonsoft.uranus.data.config.properties;


public class HikariDataSourceProperty extends DataSourceProperty {

    // 连接池返回的链接是否自动提交（commit）
    private boolean autoCommit = true;

    // 等待连接池返回链接的超时时间（毫秒），如果值小于250毫秒则会重置为30秒
    private long connectionTimeout = 30000;

    // 链接允许闲置的时间
    private long idleTimeout = 600000;

    // 保持连接的时长，最小为30秒，0为关闭，默认为0
    private long keepaliveTime = 0;

    // 链接最长生命周期，最小值为30秒，小于则会重置为30分钟
    private long maxLifetime = 1800000;

    // 最小链接数量，默认等于maximumPoolSize
    private int minimumIdle = 10;

    // 连接池最大容量，超过后会等待，等待超时时间为connectionTimeout
    private int maximumPoolSize = 10;
    
    // 连接池名称
    private String poolName = null;

    // 是否要创建只读连接池（读写分离时使用）
    private boolean readonly = false;

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getKeepaliveTime() {
        return keepaliveTime;
    }

    public void setKeepaliveTime(long keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    
}
