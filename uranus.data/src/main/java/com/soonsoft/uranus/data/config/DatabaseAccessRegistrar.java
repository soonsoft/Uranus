package com.soonsoft.uranus.data.config;

import java.util.Map;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.EnableDatabaseAccess;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class DatabaseAccessRegistrar
        implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttrs = annotationMetadata
                .getAnnotationAttributes(EnableDatabaseAccess.class.getName());
        String dataSourceNames = (String) annotationAttrs.get("dataSourceNames");
        String primaryName = (String) annotationAttrs.get("primaryName");
        String mybatisMapperLocations = (String) annotationAttrs.get("mybatisMapperLocations");

        if (StringUtils.isEmpty(dataSourceNames)) {
            throw new IllegalStateException("the dataSourceNames of EnableDatabaseAccess is required.");
        }

        String[] dataSourceNameArray = dataSourceNames.split(",");
        if (StringUtils.isEmpty(primaryName)) {
            primaryName = dataSourceNameArray[0].trim();
        }
        for (int i = 0; i < dataSourceNameArray.length; i++) {
            String dataSourceName = dataSourceNameArray[i].trim();
            boolean primary = primaryName.equals(dataSourceName);
            registerTransactionManager(dataSourceName, registry, primary);
            registerDatabaseAccess(dataSourceName, mybatisMapperLocations, registry, primary);
        }
    }

    protected void registerTransactionManager(String dataSourceName, BeanDefinitionRegistry registry, boolean primary) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        builder.addConstructorArgReference(dataSourceName);

        BeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setPrimary(primary);
        String beanName = dataSourceName + "TransactionManager";
        registry.registerBeanDefinition(beanName, beanDefinition);

    }

    protected void registerDatabaseAccess(String dataSourceName, String mybatisMapperLocations, BeanDefinitionRegistry registry, boolean primary) {
        if (StringUtils.isEmpty(mybatisMapperLocations)) {
            throw new IllegalArgumentException("the parameter mybatisMapperLocations is required.");
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DatabaseAccessBeanFactory.class);
        builder.addConstructorArgReference(dataSourceName);
        builder.addPropertyValue("mapperLocations", mybatisMapperLocations.split(","));

        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        String beanName = dataSourceName + "Access";
        registry.registerBeanDefinition(beanName, beanDefinition);
    }
}
