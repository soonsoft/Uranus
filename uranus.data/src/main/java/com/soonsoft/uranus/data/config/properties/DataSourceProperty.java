package com.soonsoft.uranus.data.config.properties;

import javax.sql.DataSource;

public abstract class DataSourceProperty {

    private Class<? extends DataSource> dataSourceType;
    
    private String driverClassName;

    private String url;

    private String username;

    private String password;

    protected DataSourceProperty(Class<? extends DataSource> dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Class<? extends DataSource> getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Class<? extends DataSource> dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

}
