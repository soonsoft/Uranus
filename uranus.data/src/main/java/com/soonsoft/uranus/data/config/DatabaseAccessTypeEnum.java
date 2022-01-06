package com.soonsoft.uranus.data.config;

import com.soonsoft.uranus.data.config.factorybean.BaseDatabaseAccessFactory;
import com.soonsoft.uranus.data.config.factorybean.MybatisDatabaseAccessFactory;
import com.soonsoft.uranus.data.config.factorybean.JdbcTemplateDatabaseAccessFactory;

public enum DatabaseAccessTypeEnum {

    MYBATIS(MybatisDatabaseAccessFactory.class),
    JDBC(JdbcTemplateDatabaseAccessFactory.class),
    ;

    private Class<? extends BaseDatabaseAccessFactory> factoryClass;

    private DatabaseAccessTypeEnum(Class<? extends BaseDatabaseAccessFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    public Class<? extends BaseDatabaseAccessFactory> getFactoryClass() {
        return this.factoryClass;
    }
    
}
