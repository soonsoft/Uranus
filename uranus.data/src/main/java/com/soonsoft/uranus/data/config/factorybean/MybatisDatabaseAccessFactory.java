package com.soonsoft.uranus.data.config.factorybean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;
import javax.sql.DataSource;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.config.exception.UranusMybatisConfigurationException;
import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.interceptor.PagingInterceptor;
import com.soonsoft.uranus.data.service.mybatis.sqltype.UUIDTypeHandler;
import com.soonsoft.uranus.data.service.mybatis.mapper.MappedStatementRegistry;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.GetByPrimary;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Insert;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.InsertSelective;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Update;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.UpdateSelective;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Delete;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

public class MybatisDatabaseAccessFactory extends BaseDatabaseAccessFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(MybatisDatabaseAccessFactory.class);
    private String[] mapperLocations;
    private String[] entityClassPackages;
    private MappedStatementRegistry mappedStatementRegistry;

    public MybatisDatabaseAccessFactory(DataSource dataSource) {
        super(dataSource);
        mappedStatementRegistry = new MappedStatementRegistry("uranus");
        mappedStatementRegistry.addSQLMapperHandler(
            new Insert(),
            new InsertSelective(),
            new Update(),
            new UpdateSelective(),
            new Delete(),
            new GetByPrimary()
        );
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String[] getEntityClassPackages() {
        return entityClassPackages;
    }

    public void setEntityClassPackages(String[] entityClassPackages) {
        this.entityClassPackages = entityClassPackages;
    }

    //#region FactoryBean

    @Override
    public IDatabaseAccess<?> getObject() throws Exception {
        SqlSessionFactory sessionFactory = createSessionFactory();
        SqlSessionTemplate sqlTemplate = new SqlSessionTemplate(sessionFactory);

        MybatisDatabaseAccess databaseAccess = new MybatisDatabaseAccess();
        databaseAccess.setTemplate(sqlTemplate);
        databaseAccess.setMappedStatementRegistry(getMappedStatementRegistry());
        return databaseAccess;
    }

    @Override
    public Class<?> getObjectType() {
        return IDatabaseAccess.class;
    }

    //#endregion

    protected MappedStatementRegistry getMappedStatementRegistry() {
        return this.mappedStatementRegistry;
    }

    protected PagingInterceptor createPagingInterceptor() {
        DataSource dataSource = this.getDataSource();
        if(dataSource == null) {
            return null;
        }

        IPagingDailect pagingDailect = getPagingDailect(dataSource);
        if(pagingDailect != null) {
            return new PagingInterceptor(pagingDailect);
        }
        return null;
    }

    protected TypeHandler<?>[] getTypeHandlers() {
        return new TypeHandler<?>[] {
            new UUIDTypeHandler()
        };
    }

    protected Resource[] loadXmlMapperResources(ResourcePatternResolver resolver, String[] xmlMapperLocations) throws IOException {
        ArrayList<Resource> resourceList = new ArrayList<>(20);
        for (String location : xmlMapperLocations) {
            if(!StringUtils.isBlank(location)) {
                CollectionUtils.addAll(resourceList, resolver.getResources(location.trim()));
            }
        }

        return resourceList.toArray(new Resource[0]);
    }

    protected Class<?>[] loadEntityClasses(ResourcePatternResolver resolver, String[] entityLocations) 
            throws IOException, ClassNotFoundException {

        List<Class<?>> entityClassList = new ArrayList<>(20);
        String[] entityPackages = getEntityClassPackages();
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        for(int i = 0; i < entityPackages.length; i++) {
            String entityPackage = entityPackages[i];
            if(!StringUtils.isBlank(entityPackage)) {
                String packageSearchPath = StringUtils.format("classpath*:{0}/**/*.class", entityPackage.trim().replace(".", "/"));
                Resource[] resources = resolver.getResources(packageSearchPath);
                if(resources != null) {
                    for(int j = 0; j < resources.length; j++) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resources[j]);
                        if(metadataReader.getAnnotationMetadata().hasAnnotation(Table.class.getName())) {
                            String className = metadataReader.getClassMetadata().getClassName();
                            Class<?> entityClass = ClassUtils.forName(className, metadataReaderFactory.getResourceLoader().getClassLoader());
                            entityClassList.add(entityClass);
                        }
                    }
                }
            }
        }

        return entityClassList.toArray(new Class<?>[0]);
    }

    private SqlSessionFactory createSessionFactory() {
        // http://www.mybatis.org/mybatis-3/zh/configuration.html
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setCacheEnabled(true);
        mybatisConfig.setLogPrefix("[Mybatis-SQL]");
        mybatisConfig.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        mybatisConfig.setShrinkWhitespacesInSql(true);

        PagingInterceptor pagingInterceptor = createPagingInterceptor();
        if(pagingInterceptor != null) {
            mybatisConfig.addInterceptor(pagingInterceptor);
        } else {
            LOGGER.warn("the PagingInterceptor is null.");
        }

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setTypeHandlers(getTypeHandlers());

        try {
            PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
            // 处理 xml mapper文件
            if (getMapperLocations() != null) {
                Resource[] xmlMapperResources = loadXmlMapperResources(pathResolver, getMapperLocations());
                if(xmlMapperResources.length > 0) {
                    bean.setMapperLocations(xmlMapperResources);
                } else {
                    LOGGER.warn("[mybatisMapperLocations] can not load Resources.");
                }
            }
            bean.setConfiguration(mybatisConfig);

            SqlSessionFactory sessionFactory = bean.getObject();

            // 自动注入DML操作
            if(getEntityClassPackages() != null) {
                Class<?>[] entityClasses = loadEntityClasses(pathResolver, getEntityClassPackages());
                if(entityClasses.length > 0) {
                    mappedStatementRegistry.initial(entityClasses);
                    mappedStatementRegistry.register(mybatisConfig);
                } else {
                    LOGGER.warn("[entityClassLocations] can not load entity classes.");
                }
            }

            return sessionFactory;
        } catch (Exception e) {
            throw new UranusMybatisConfigurationException(e);
        }
    }
    
}
