package com.soonsoft.uranus.data;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import com.soonsoft.uranus.data.config.DatabaseAccessRegistrar;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(DatabaseAccessRegistrar.class)
public @interface EnableDatabaseAccess {

    /** 对应DataSource配置节点名称，多个数据源可以用“,”隔开 */
    String dataSourceNames() default "master";

    /** 主库数据源的配置节点名称 */
    String primaryName() default "master";

    /** Mybatis SQL Mapper xml path */
    String mybatisMapperLocations() default "classpath*:/sql/**/*Mapper.xml";

}
