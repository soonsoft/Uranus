package com.soonsoft.uranus.data.config;

import com.soonsoft.uranus.data.config.factorybean.BaseDatabaseAccessFactory;
import com.soonsoft.uranus.data.config.factorybean.MybatisDatabaseAccessFactory;
import com.soonsoft.uranus.data.config.factorybean.JdbcTemplateDatabaseAccessFactory;
import com.soonsoft.uranus.data.config.factorybean.NamedParameterJdbcTemplateDatabaseAccessFactory;

public enum DatabaseAccessTypeEnum {

    MYBATIS(MybatisDatabaseAccessFactory.class),
    JDBC(JdbcTemplateDatabaseAccessFactory.class),
    JDBC_NAMED_PARAMETER(NamedParameterJdbcTemplateDatabaseAccessFactory.class),
    ;

    private Class<? extends BaseDatabaseAccessFactory> factoryClass;

    private DatabaseAccessTypeEnum(Class<? extends BaseDatabaseAccessFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    public Class<? extends BaseDatabaseAccessFactory> getFactoryClass() {
        return this.factoryClass;
    }
    
}
