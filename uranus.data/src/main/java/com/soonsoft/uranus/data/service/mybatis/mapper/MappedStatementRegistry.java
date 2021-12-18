package com.soonsoft.uranus.data.service.mybatis.mapper;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.Configuration;

public class MappedStatementRegistry {

    private final static String DOT = ".";

    protected MapperBuilderAssistant builderAssistant;

    public MappedStatementRegistry(String namespace, Configuration configuration) {
        this.builderAssistant = new MapperBuilderAssistant(configuration, "AUTO_INJECT");
        this.builderAssistant.setCurrentNamespace(namespace);
    }

    public MappedStatement addInsertMappedStatement(
            String id, 
            SqlSource sqlSource, 
            Class<?> parameterType,
            KeyGenerator keyGenerator,
            String keyProperty, 
            String keyColumn) {
        
        return addMappedStatement(
            id, sqlSource, SqlCommandType.INSERT, parameterType, null, Integer.class, 
            keyGenerator, keyProperty, keyColumn);
    }

    public MappedStatement addUpdateMappedStatement(String id, SqlSource sqlSource, Class<?> parameterType) {
        return addMappedStatement(
            id, sqlSource, SqlCommandType.UPDATE, parameterType, null, Integer.class, 
            NoKeyGenerator.INSTANCE, null, null);
    }

    public MappedStatement addDeleteMappedStatement(String id, SqlSource sqlSource, Class<?> parameterType) {
        return addMappedStatement(
            id, sqlSource, SqlCommandType.DELETE, parameterType, null, Integer.class, 
            NoKeyGenerator.INSTANCE, null, null);
    }

    public MappedStatement addSelectMappedStatement(
            String id, 
            SqlSource sqlSource, 
            Class<?> parameterType,
            Class<?> resultType) {
        
        return addMappedStatement(
            id, sqlSource, SqlCommandType.SELECT, parameterType, null, resultType, 
            NoKeyGenerator.INSTANCE, null, null);

    }

    protected MappedStatement addMappedStatement(
            String id, 
            SqlSource sqlSource,
            SqlCommandType sqlCommandType, 
            Class<?> parameterType,
            String resultMap, 
            Class<?> resultType, 
            KeyGenerator keyGenerator,
            String keyProperty, 
            String keyColumn) {

        Configuration configuration = builderAssistant.getConfiguration();
        String statementName = builderAssistant.getCurrentNamespace() + DOT + id;
        if (configuration.hasStatement(statementName)) {
            //logger.warn(LEFT_SQ_BRACKET + statementName + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for [" + getClass() + RIGHT_SQ_BRACKET);
            //return null;
        }
        /* 缓存逻辑处理 */
        boolean flushCache = true;
        boolean useCache = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            flushCache = false;
            useCache = true;
        }

        return builderAssistant.addMappedStatement(
            id, 
            sqlSource, 
            StatementType.PREPARED, 
            sqlCommandType,
            null, 
            null, 
            null, 
            parameterType, 
            resultMap, 
            resultType,
            null, 
            flushCache, 
            useCache, 
            false, 
            keyGenerator, 
            keyProperty, 
            keyColumn,
            configuration.getDatabaseId(), 
            configuration.getDefaultScriptingLanguageInstance(), 
            null);
    } 
    
}
