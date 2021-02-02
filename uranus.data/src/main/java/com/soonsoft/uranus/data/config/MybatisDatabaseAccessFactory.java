package com.soonsoft.uranus.data.config;

import java.util.ArrayList;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.config.exception.UranusMybatisConfigurationException;
import com.soonsoft.uranus.data.paging.PagingInterceptor;
import com.soonsoft.uranus.data.paging.postgresql.PostgreSQLPagingDailect;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class MybatisDatabaseAccessFactory extends BaseDatabaseAccessFactory implements FactoryBean<IDatabaseAccess> {

    public MybatisDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public IDatabaseAccess getObject() throws Exception {
        SqlSessionFactory sessionFactory = createSessionFactory();
        SqlSessionTemplate sqlTemplate = new SqlSessionTemplate(sessionFactory);

        MybatisDatabaseAccess databaseAccess = new MybatisDatabaseAccess();
        databaseAccess.setSqlTemplate(sqlTemplate);
        return databaseAccess;
    }

    @Override
    public Class<?> getObjectType() {
        return IDatabaseAccess.class;
    }

    private SqlSessionFactory createSessionFactory() {
        // http://www.mybatis.org/mybatis-3/zh/configuration.html
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setCacheEnabled(true);
        mybatisConfig.setLogPrefix("[Mybatis-SQL]");
        mybatisConfig.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        mybatisConfig.addInterceptor(new PagingInterceptor(new PostgreSQLPagingDailect()));

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setTypeAliasesPackage("com.soonsoft.uranus.services.**.dto");

        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        String[] mapperLocations = getMapperLocations();
        if (mapperLocations != null) {
            ArrayList<Resource> resourceList = new ArrayList<>(20);
            for (int i = 0; i < mapperLocations.length; i++) {
                String location = mapperLocations[i].trim();
                CollectionUtils.addAll(resourceList, pathResolver.getResource(location));
            }
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
