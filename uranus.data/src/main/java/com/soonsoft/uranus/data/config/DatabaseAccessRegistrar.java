package com.soonsoft.uranus.data.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.EnableDatabaseAccess;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.NonNull;

public class DatabaseAccessRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata, @NonNull BeanDefinitionRegistry registry) {
        Guard.notNull(annotationMetadata, "the arguments annotationMetadata is required.");
        Guard.notNull(registry, "the arguments registry is required.");

        Map<String, Object> annotationAttrs = annotationMetadata.getAnnotationAttributes(EnableDatabaseAccess.class.getName());
        if(annotationAttrs == null) {
            throw new IllegalStateException("cannot find the annotation class [EnableDatabaseAccess].");
        }

        String primaryName = (String) annotationAttrs.get("primaryName");
        String[] mybatisMapperLocations = (String[]) annotationAttrs.get("mybatisMapperLocations");
        String[] entityClassPackages = (String[]) annotationAttrs.get("entityClassPackages");
        DatabaseAccessTypeEnum type = (DatabaseAccessTypeEnum)annotationAttrs.get("type");
        String[] dataSourceNames = findDataSourceBeanNames(registry);

        if (CollectionUtils.isEmpty(dataSourceNames)) {
            return;
        }

        if(StringUtils.isBlank(primaryName)) {
            primaryName = dataSourceNames[0];
        }

        if (StringUtils.isEmpty(primaryName)) {
            primaryName = dataSourceNames[0].trim();
        }

        for (int i = 0; i < dataSourceNames.length; i++) {
            String dataSourceName = dataSourceNames[i].trim();
            boolean primary = primaryName.equals(dataSourceName);
            registerTransactionManager(dataSourceName, registry, primary);
            registerDatabaseAccess(type, dataSourceName, mybatisMapperLocations, entityClassPackages, registry, primary);
        }
    }

    protected void registerTransactionManager(String dataSourceName, BeanDefinitionRegistry registry, boolean primary) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        builder.addConstructorArgReference(dataSourceName);

        BeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setPrimary(primary);
        String beanName = dataSourceName + "TransactionManager";
        registry.registerBeanDefinition(beanName, beanDefinition);
        if (primary) {
            registry.registerAlias(dataSourceName, "transactionManager");
        }
    }

    protected void registerDatabaseAccess(
            DatabaseAccessTypeEnum type, 
            String dataSourceName, 
            String[] mybatisMapperLocations, 
            String[] entityClassPackages, 
            BeanDefinitionRegistry registry, 
            boolean primary) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(type.getFactoryClass());
        builder.addConstructorArgReference(dataSourceName);
        if(DatabaseAccessTypeEnum.MYBATIS == type) {
            if (mybatisMapperLocations == null || mybatisMapperLocations.length == 0) {
                throw new IllegalArgumentException("the parameter mybatisMapperLocations is required.");
            }
            builder.addPropertyValue("mapperLocations", mybatisMapperLocations);

            if(entityClassPackages != null && entityClassPackages.length > 0) {
                builder.addPropertyValue("entityClassPackages", entityClassPackages);
            }
        }

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        String beanName = dataSourceName + "Access";
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private String[] findDataSourceBeanNames(BeanDefinitionRegistry registry) {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();

        if(CollectionUtils.isEmpty(beanDefinitionNames)) {
            return null;
        }

        String dataSourceClassName = DataSource.class.getSimpleName();
        List<String> dataSourceBeanNameList = new ArrayList<>();
        for(String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            if(beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                MethodMetadata methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
                if(methodMetadata != null) {
                    String returnTypeName = methodMetadata.getReturnTypeName();
                    if(!StringUtils.isEmpty(returnTypeName) && returnTypeName.endsWith(dataSourceClassName)) {
                        dataSourceBeanNameList.add(beanDefinitionName);
                    }
                }
            }
        }
        return dataSourceBeanNameList.toArray(new String[0]);
    }
}
