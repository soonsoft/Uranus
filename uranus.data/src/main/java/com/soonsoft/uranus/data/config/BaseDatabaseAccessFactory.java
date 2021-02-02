package com.soonsoft.uranus.data.config;

import javax.sql.DataSource;

public abstract class BaseDatabaseAccessFactory {

    private DataSource dataSource;
    private String[] mapperLocations;

    public BaseDatabaseAccessFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }
    
}
