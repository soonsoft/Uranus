package com.soonsoft.uranus.data.config.factorybean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.common.DatabaseTypeEnum;
import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.data.config.exception.UranusMybatisConfigurationException;
import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.data.paging.mysql.MySQLPagingDailect;
import com.soonsoft.uranus.data.paging.postgresql.PostgreSQLPagingDailect;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.interceptor.PagingInterceptor;
import com.soonsoft.uranus.data.service.mybatis.sqltype.UUIDTypeHandler;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class MybatisDatabaseAccessFactory extends BaseDatabaseAccessFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(MybatisDatabaseAccessFactory.class);

    private final static Map<String, Func0<IPagingDailect>> PagingDailectFactoryMap = new HashMap<>() {
        {
            put(DatabaseTypeEnum.MySQL.getDatabaseName(), () -> new MySQLPagingDailect());
            put(DatabaseTypeEnum.PostgreSQL.getDatabaseName(), () -> new PostgreSQLPagingDailect());
        }
    };
    private String[] mapperLocations;

    public MybatisDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    //#region FactoryBean

    @Override
    public IDatabaseAccess<?> getObject() throws Exception {
        SqlSessionFactory sessionFactory = createSessionFactory();
        SqlSessionTemplate sqlTemplate = new SqlSessionTemplate(sessionFactory);

        MybatisDatabaseAccess databaseAccess = new MybatisDatabaseAccess();
        databaseAccess.setTemplate(sqlTemplate);
        return databaseAccess;
    }

    @Override
    public Class<?> getObjectType() {
        return IDatabaseAccess.class;
    }

    //#endregion

    protected PagingInterceptor createPagingInterceptor() {
        DataSource dataSource = this.getDataSource();
        if(dataSource == null) {
            return null;
        }

        String dbName = DatabaseTypeEnum.findDatabaseName(DataSourceFactory.getDriverClassName(dataSource));
        if(dbName != null) {
            Func0<IPagingDailect> factory = PagingDailectFactoryMap.get(dbName);
            if(factory != null) {
                return new PagingInterceptor(factory.call());
            }
        }
        return null;
    }

    protected TypeHandler<?>[] getTypeHandlers() {
        return new TypeHandler<?>[] {
            new UUIDTypeHandler()
        };
    }

    private SqlSessionFactory createSessionFactory() throws IOException {
        // http://www.mybatis.org/mybatis-3/zh/configuration.html
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setCacheEnabled(true);
        mybatisConfig.setLogPrefix("[Mybatis-SQL]");
        mybatisConfig.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);

        PagingInterceptor pagingInterceptor = createPagingInterceptor();
        if(pagingInterceptor != null) {
            mybatisConfig.addInterceptor(pagingInterceptor);
        } else {
            LOGGER.warn("the PagingInterceptor is null.");
        }

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setTypeAliasesPackage("com.soonsoft.uranus.services.**.po");
        bean.setTypeHandlers(getTypeHandlers());

        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        String[] mapperLocations = getMapperLocations();
        if (mapperLocations != null) {
            ArrayList<Resource> resourceList = new ArrayList<>(20);
            for (int i = 0; i < mapperLocations.length; i++) {
                String location = mapperLocations[i].trim();
                CollectionUtils.addAll(resourceList, pathResolver.getResources(location));
            }
            bean.setMapperLocations(resourceList.toArray(new Resource[0]));
        }
        bean.setConfiguration(mybatisConfig);

        try {
            SqlSessionFactory sessionFactory = bean.getObject();
            return sessionFactory;
        } catch (Exception e) {
            throw new UranusMybatisConfigurationException(e);
        }
    }
    
}
