package com.soonsoft.uranus.data.config.factorybean;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.IDatabaseAccess;

import org.springframework.beans.factory.FactoryBean;

public abstract class BaseDatabaseAccessFactory implements FactoryBean<IDatabaseAccess<?>> {

    private DataSource dataSource;

    public BaseDatabaseAccessFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    
}
