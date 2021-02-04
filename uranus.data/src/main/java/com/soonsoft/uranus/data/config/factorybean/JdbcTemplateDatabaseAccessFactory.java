package com.soonsoft.uranus.data.config.factorybean;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.jdbc.JdbcTemplateDatabaseAccess;

public class JdbcTemplateDatabaseAccessFactory extends BaseDatabaseAccessFactory {

    public JdbcTemplateDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public IDatabaseAccess<?> getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcTemplateDatabaseAccess.class;
    }
    
}
