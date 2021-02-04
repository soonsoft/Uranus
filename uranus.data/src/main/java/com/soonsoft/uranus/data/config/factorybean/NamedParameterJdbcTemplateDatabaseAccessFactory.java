package com.soonsoft.uranus.data.config.factorybean;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.jdbc.NamedParameterJdbcTemplateDatabaseAccess;

public class NamedParameterJdbcTemplateDatabaseAccessFactory extends BaseDatabaseAccessFactory {

    public NamedParameterJdbcTemplateDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public IDatabaseAccess<?> getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return NamedParameterJdbcTemplateDatabaseAccess.class;
    }
    
}
