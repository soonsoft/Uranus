package com.soonsoft.uranus.data.service.mybatis.mapper;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

public class MappedStatementRegistry {

    private final static String DOT = ".";

    protected MapperBuilderAssistant builderAssistant;

    protected MappedStatement addMappedStatement(
            Class<?> mapperClass, 
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
        String statementName = mapperClass.getName() + DOT + id;
        if (configuration.hasStatement(statementName)) {
            //logger.warn(LEFT_SQ_BRACKET + statementName + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for [" + getClass() + RIGHT_SQ_BRACKET);
            //return null;
        }
        /* 缓存逻辑处理 */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType,
            null, null, null, parameterType, resultMap, resultType,
            null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
            configuration.getDatabaseId(), languageDriver, null);
    } 
    
}
