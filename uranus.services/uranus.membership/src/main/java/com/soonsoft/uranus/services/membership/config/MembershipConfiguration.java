package com.soonsoft.uranus.services.membership.config;

import javax.sql.DataSource;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.services.membership.config.properties.MembershipProperties;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MembershipConfiguration
 */
@Configurable
@EnableConfigurationProperties(MembershipProperties.class)
public class MembershipConfiguration {

    public IDatabaseAccess createDatabaseAccess(DataSource dataSource, Configuration mybatisConfig) throws Exception {
        SqlSessionFactory sessionFactory = createSqlSessionFactory(dataSource, mybatisConfig);
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sessionFactory);

        MybatisDatabaseAccess dba = new MybatisDatabaseAccess();
        dba.setSqlTemplate(sessionTemplate);
        return dba;
    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource, Configuration mybatisConfig) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.soonsoft.uranus.services.membership.dto");
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/sql/**/*Mapper.xml"));
        
        if(mybatisConfig == null) {
            mybatisConfig = createConfiguration();
        }
        bean.setConfiguration(mybatisConfig);
        
        return bean.getObject();
    }

    private Configuration createConfiguration() {
        // http://www.mybatis.org/mybatis-3/zh/configuration.html
        Configuration config = new Configuration();
        config.setCacheEnabled(true);
        config.setLogPrefix("[Mybatis-SQL]");
        config.setLogImpl(Slf4jImpl.class);
        return config;
    }

}