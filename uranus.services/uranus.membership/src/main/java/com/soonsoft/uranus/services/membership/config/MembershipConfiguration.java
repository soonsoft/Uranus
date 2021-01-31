package com.soonsoft.uranus.services.membership.config;

import javax.annotation.Resource;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.security.config.BaseSecurityConfiguration;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.services.membership.config.properties.MembershipProperties;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable
public class MembershipConfiguration extends BaseSecurityConfiguration {

    @Resource
    private MembershipProperties membershipProperties;

    @Override
    public SecurityProperties getSecurityProperties() {
        return membershipProperties;
    }

    @Bean
    public UserServiceFactory userServiceFactory(PasswordEncoder passwordEncoder) {
        String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new UserServiceFactory(applicationContext.getBean(IDatabaseAccess.class), passwordEncoder);
        }

        return new UserServiceFactory(applicationContext, membershipDatabaseAccessName, passwordEncoder);
    }

    @Bean
    public RoleServiceFactory roleServiceFactory() {
        String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new RoleServiceFactory(applicationContext.getBean(IDatabaseAccess.class));
        }

        return new RoleServiceFactory(applicationContext, membershipDatabaseAccessName);
    }

    @Bean
    public FunctionServiceFactory functionServiceFactory() {
    String membershipDatabaseAccessName = membershipProperties.getDatabaseAccessName();
        ApplicationContext applicationContext = getApplicationContext();
        if(StringUtils.isEmpty(membershipDatabaseAccessName)) {
            return new FunctionServiceFactory(applicationContext.getBean(IDatabaseAccess.class));
        }

        return new FunctionServiceFactory(applicationContext, membershipDatabaseAccessName);
    }

    // public IDatabaseAccess createDatabaseAccess(DataSource dataSource, Configuration mybatisConfig) throws Exception {
    //     SqlSessionFactory sessionFactory = createSqlSessionFactory(dataSource, mybatisConfig);
    //     SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sessionFactory);

    //     MybatisDatabaseAccess dba = new MybatisDatabaseAccess();
    //     dba.setSqlTemplate(sessionTemplate);
    //     return dba;
    // }

    // private SqlSessionFactory createSqlSessionFactory(DataSource dataSource, Configuration mybatisConfig) throws Exception {
    //     SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    //     bean.setDataSource(dataSource);
    //     bean.setTypeAliasesPackage("com.soonsoft.uranus.services.membership.dto");
    //     bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/sql/**/*Mapper.xml"));
        
    //     if(mybatisConfig == null) {
    //         mybatisConfig = createConfiguration();
    //     }
    //     bean.setConfiguration(mybatisConfig);
        
    //     return bean.getObject();
    // }

    // private Configuration createConfiguration() {
    //     // http://www.mybatis.org/mybatis-3/zh/configuration.html
    //     Configuration config = new Configuration();
    //     config.setCacheEnabled(true);
    //     config.setLogPrefix("[Mybatis-SQL]");
    //     config.setLogImpl(Slf4jImpl.class);
    //     return config;
    // }

}