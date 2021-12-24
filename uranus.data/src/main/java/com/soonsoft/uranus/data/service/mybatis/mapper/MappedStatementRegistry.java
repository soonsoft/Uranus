package com.soonsoft.uranus.data.service.mybatis.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.soonsoft.uranus.data.service.meta.TableInfo;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

public class MappedStatementRegistry {

    private final static String DOT = ".";
    private final static String RESOUCE_VALUE = "AUTO_INJECT";
    private final static String DEFAULT_NAMESPACE = "uranus";
    private final List<ISQLMapper> mapperHandlerList;

    public MappedStatementRegistry() {
        this.mapperHandlerList = new ArrayList<>(20);
    }

    public void addSQLMapperHandler(ISQLMapper... sqlMappers) {
        if(sqlMappers == null || sqlMappers.length == 0) {
            throw new IllegalArgumentException("the parameter sqlMapper is required.");
        }
        for(int i = 0; i < sqlMappers.length; i++) {
            this.mapperHandlerList.add(sqlMappers[i]);
        }
    }

    public void addSQLMapperHandler(Collection<ISQLMapper> sqlMappers) {
        mapperHandlerList.addAll(sqlMappers);
    }

    public void register(Configuration configuration, Class<?> entityClasses) {
        register(configuration, DEFAULT_NAMESPACE, entityClasses);
    }

    public void register(Configuration configuration, String namespace, Class<?>... entityClasses) {
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, RESOUCE_VALUE);
        for(int i = 0; i < entityClasses.length; i++) {
            Class<?> poClass = entityClasses[i];
            TableInfo tableInfo = null; // getTableInfo by poClass
            String currentNamespace = namespace + DOT + tableInfo.getTableName();
            builderAssistant.setCurrentNamespace(currentNamespace);

            for(ISQLMapper mapperHandler : mapperHandlerList) {
                mapperHandler.add(builderAssistant, poClass, tableInfo);
            }
        }
    }
    
}
