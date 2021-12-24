package com.soonsoft.uranus.data.service.mybatis.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.meta.loader.ITableInfoLoader;
import com.soonsoft.uranus.data.service.meta.loader.jpa.JAPTableInfoLoader;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

public class MappedStatementRegistry {

    private final static String DOT = ".";
    private final static String RESOUCE_VALUE = "AUTO_INJECT";
    private final static String DEFAULT_NAMESPACE = "uranus";
    private final List<ISQLMapper> mapperHandlerList;

    private final ITableInfoLoader tableInfoLoader;

    public MappedStatementRegistry() {
        this(new JAPTableInfoLoader());
    }

    public MappedStatementRegistry(ITableInfoLoader tableInfoLoader) {
        this.mapperHandlerList = new ArrayList<>(20);
        this.tableInfoLoader = tableInfoLoader;
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

    public void register(Configuration configuration, Class<?>... entityClasses) {
        register(configuration, DEFAULT_NAMESPACE, entityClasses);
    }

    public void register(Configuration configuration, String namespace, Class<?>... entityClasses) {
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, RESOUCE_VALUE);
        for(int i = 0; i < entityClasses.length; i++) {
            Class<?> entityClass = entityClasses[i];
            TableInfo tableInfo = tableInfoLoader.load(entityClass);
            tableInfo.setAlias(StringUtils.format("{0}_t{1}", namespace, i + 1));

            String currentNamespace = namespace + DOT + tableInfo.getTableName();
            builderAssistant.setCurrentNamespace(currentNamespace);

            for(ISQLMapper mapperHandler : mapperHandlerList) {
                mapperHandler.add(builderAssistant, entityClass, tableInfo);
            }
        }
    }
    
}
