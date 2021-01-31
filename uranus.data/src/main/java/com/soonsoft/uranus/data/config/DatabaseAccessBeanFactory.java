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

public class DatabaseAccessBeanFactory implements FactoryBean<IDatabaseAccess> {

    private DataSource dataSource;
    private String[] mapperLocations;

    public DatabaseAccessBeanFactory(DataSource dataSource) {
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
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.soonsoft.uranus.services.**.dto");

        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        ArrayList<Resource> resourceList = new ArrayList<>(20);
        if (mapperLocations != null) {
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
