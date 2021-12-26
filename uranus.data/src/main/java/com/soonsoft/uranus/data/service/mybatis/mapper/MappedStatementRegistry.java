package com.soonsoft.uranus.data.service.mybatis.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.meta.loader.ITableInfoLoader;
import com.soonsoft.uranus.data.service.meta.loader.jpa.JAPTableInfoLoader;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

public class MappedStatementRegistry {

    private final static String DOT = ".";
    private final static String RESOUCE_VALUE = "AUTO-INJECT";

    private final String namespace;
    private final List<ISQLMapper> mapperHandlerList;
    private final ITableInfoLoader tableInfoLoader;
    private final Map<Class<?>, TableInfo> tableInfoMap;
    private final Map<String, Class<?>> entityClassNameMap;

    private Configuration configuration;

    public MappedStatementRegistry(String namespace) {
        this(new JAPTableInfoLoader(), namespace);
    }

    public TableInfo findTableInfo(String entityClassName) {
        Class<?> entityClass = entityClassNameMap.get(entityClassName);
        if(entityClass != null) {
            return findTableInfo(entityClass);
        }
        return null;
    }

    public TableInfo findTableInfo(Class<?> entityClass) {
        return tableInfoMap.get(entityClass);
    }

    public MappedStatementRegistry(ITableInfoLoader tableInfoLoader, String namespace) {
        this.mapperHandlerList = new ArrayList<>(20);
        this.tableInfoMap = new ConcurrentHashMap<>();
        this.entityClassNameMap = new ConcurrentHashMap<>();
        this.tableInfoLoader = tableInfoLoader;
        this.namespace = namespace;
    }

    public String getNamespace(TableInfo tableInfo) {
        return namespace + DOT + tableInfo.getTableName();
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
        Guard.notEmpty(sqlMappers, "the parameter sqlMappers is required.");
        mapperHandlerList.addAll(sqlMappers);
    }

    public void initial(Class<?>... entityClasses) {
        for(int i = 0; i < entityClasses.length; i++) {
            Class<?> entityClass = entityClasses[i];
            TableInfo tableInfo = tableInfoLoader.load(entityClass);
            tableInfo.setAlias(StringUtils.format("{0}_t{1}", namespace, String.valueOf(i + 1)));

            tableInfoMap.put(entityClass, tableInfo);
            entityClassNameMap.put(entityClass.getName(), entityClass);
        }
    }

    public void register(Configuration configuration) {
        this.configuration = configuration;
        register(mapperHandlerList);
    }

    public void registerSQLMapperHandler(ISQLMapper... sqlMappers) {
        List<ISQLMapper> sqlMapperList = new ArrayList<>();
        for(ISQLMapper sqlMapper : sqlMappers) {
            sqlMapperList.add(sqlMapper);
        }
        registerSQLMapperHandler(sqlMapperList);
    }

    public void registerSQLMapperHandler(Collection<ISQLMapper> sqlMappers) {
        addSQLMapperHandler(sqlMappers);
        register(sqlMappers);
    }

    private void register(Collection<ISQLMapper> sqlMappers) {
        String resource = namespace.toUpperCase() + "-" + RESOUCE_VALUE;
        for (Map.Entry<Class<?>, TableInfo> entry : tableInfoMap.entrySet()) {
            Class<?> entryClass = entry.getKey();
            TableInfo tableInfo = entry.getValue();

            String currentNamespace = getNamespace(tableInfo);
            MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, resource);
            builderAssistant.setCurrentNamespace(currentNamespace);

            for(ISQLMapper mapperHandler : mapperHandlerList) {
                mapperHandler.add(builderAssistant, entryClass, tableInfo);
            }
        }
    }
    
}
