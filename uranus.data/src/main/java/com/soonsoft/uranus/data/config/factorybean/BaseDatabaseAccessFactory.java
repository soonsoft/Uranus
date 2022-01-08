package com.soonsoft.uranus.data.config.factorybean;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.common.DatabaseTypeEnum;
import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.data.paging.mysql.MySQLPagingDailect;
import com.soonsoft.uranus.data.paging.oracle.OraclePagingDailect;
import com.soonsoft.uranus.data.paging.postgresql.PostgreSQLPagingDailect;
import com.soonsoft.uranus.data.paging.sql.SqlServerPagingDailect;

import org.springframework.beans.factory.FactoryBean;

public abstract class BaseDatabaseAccessFactory implements FactoryBean<IDatabaseAccess<?>> {

    private final static Map<String, Func0<IPagingDailect>> PagingDailectFactoryMap = new HashMap<>() {
        {
            put(DatabaseTypeEnum.MySQL.getDatabaseName(), () -> new MySQLPagingDailect());
            put(DatabaseTypeEnum.PostgreSQL.getDatabaseName(), () -> new PostgreSQLPagingDailect());
            put(DatabaseTypeEnum.Oracle.getDatabaseName(), () -> new OraclePagingDailect());
            put(DatabaseTypeEnum.SQLServer.getDatabaseName(), () -> new SqlServerPagingDailect());
        }
    };

    private DataSource dataSource;

    public BaseDatabaseAccessFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected IPagingDailect getPagingDailect(DataSource dataSource) {
        String dbName = DatabaseTypeEnum.findDatabaseName(DataSourceFactory.getDriverClassName(dataSource));
        if(dbName != null) {
            Func0<IPagingDailect> factory = PagingDailectFactoryMap.get(dbName);
            if(factory != null) {
                return factory.call();
            }
        }
        return null;
    }
    
}
