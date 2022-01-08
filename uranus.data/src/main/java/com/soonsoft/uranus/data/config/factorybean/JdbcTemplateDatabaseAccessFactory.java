package com.soonsoft.uranus.data.config.factorybean;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.jdbc.JdbcTemplateDatabaseAccess;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcTemplateDatabaseAccessFactory extends BaseDatabaseAccessFactory {

    public JdbcTemplateDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public IDatabaseAccess<?> getObject() throws Exception {
        DataSource dataSource = getDataSource();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        JdbcTemplateDatabaseAccess jdbcDatabaseAccess = new JdbcTemplateDatabaseAccess();
        jdbcDatabaseAccess.setTemplate(jdbcTemplate);
        jdbcDatabaseAccess.setPagingDailect(getPagingDailect(dataSource));

        return jdbcDatabaseAccess;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcTemplateDatabaseAccess.class;
    }
    
}
