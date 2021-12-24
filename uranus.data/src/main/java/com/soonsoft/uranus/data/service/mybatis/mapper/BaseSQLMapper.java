package com.soonsoft.uranus.data.service.mybatis.mapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.meta.ColumnInfo;
import com.soonsoft.uranus.data.service.meta.PrimaryKey;
import com.soonsoft.uranus.data.service.meta.TableInfo;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

public abstract class BaseSQLMapper implements ISQLMapper {

    private final String mapperName;
    private MapperBuilderAssistant builderAssistant;

    public BaseSQLMapper(String name) {
        this.mapperName = name;
    }

    public String getMapperName() {
        return this.mapperName;
    }

    @Override
    public MappedStatement add(MapperBuilderAssistant builderAssistant, Class<?> entityClass, TableInfo tableInfo) {
        Guard.notNull(builderAssistant, "the parameter builderAssistant is required.");
        Guard.notNull(entityClass, "the parameter entityClass is required.");
        Guard.notNull(tableInfo, "the parameter tableInfo is required.");

        this.builderAssistant = builderAssistant;
        return add(entityClass, tableInfo);
    }

    protected abstract MappedStatement add(Class<?> entityClass, TableInfo tableInfo);
    
    protected MappedStatement addInsertMappedStatement(
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

    protected MappedStatement addUpdateMappedStatement(String id, SqlSource sqlSource, Class<?> parameterType) {
        return addMappedStatement(
            id, sqlSource, SqlCommandType.UPDATE, parameterType, null, Integer.class, 
            NoKeyGenerator.INSTANCE, null, null);
    }

    protected MappedStatement addDeleteMappedStatement(String id, SqlSource sqlSource, Class<?> parameterType) {
        return addMappedStatement(
            id, sqlSource, SqlCommandType.DELETE, parameterType, null, Integer.class, 
            NoKeyGenerator.INSTANCE, null, null);
    }

    protected MappedStatement addSelectMappedStatement(
            String id, 
            SqlSource sqlSource, 
            Class<?> parameterType,
            Class<?> resultType) {
        
        return addMappedStatement(
            id, 
            sqlSource, 
            SqlCommandType.SELECT, 
            parameterType, 
            null, 
            resultType, 
            NoKeyGenerator.INSTANCE, 
            null, 
            null);
    }

    protected MappedStatement addSelectMappedStatement(
            String id, 
            SqlSource sqlSource, 
            Class<?> parameterType,
            String resultMap) {
        
        return addMappedStatement(
            id, 
            sqlSource, 
            SqlCommandType.SELECT, 
            parameterType, 
            resultMap, 
            null, 
            NoKeyGenerator.INSTANCE, 
            null, 
            null);
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
            return null;
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

    protected ResultMap addResultMap(Class<?> entityClass, TableInfo tableInfo) {
        String id = entityClass.getSimpleName() + "_ResultMap";
        Configuration configuration = builderAssistant.getConfiguration();

        if(configuration.hasResultMap(id)) {
            return configuration.getResultMap(id);
        }

        List<ResultMapping> resultMappings = new ArrayList<>(tableInfo.getColumns().size());
        for(ColumnInfo column : tableInfo.getColumns()) {
            resultMappings.add(
                new ResultMapping.Builder(
                    configuration, 
                    column.getEntityFieldName(), 
                    column.getColumnName(), 
                    column.getEntityFieldType()).build());
        }
        ResultMap resultMap = new ResultMap.Builder(configuration, id, entityClass, resultMappings).build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    //#region SQL Clause

    protected SqlSource createSqlSource(String sql, Class<?> parameterType) {
        Configuration configuration = builderAssistant.getConfiguration();
        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        String script = StringUtils.format("<script>{0}</script>", sql);
        return languageDriver.createSqlSource(configuration, script, parameterType);
    }

    protected String getSQLParameter(ColumnInfo column) {
        String fieldName = column.getEntityFieldName();
        String jdbcType = column.getColumnType() == null ? null : column.getColumnType().name();
        // 如果该列运行使用null值，且业务逻辑中的确会传null值，则必须指定JDBCType，因为null无法推导JDBCType。
        if(!column.isNotNull() && jdbcType == null) {
            throw new IllegalArgumentException("column [] jdbcType is required.");
        }
        return "#{" 
            + fieldName 
            + (jdbcType != null ? ",jdbcType=" + jdbcType : StringUtils.Empty)
            + "}";
    }

    protected String getPrimaryWhereClause(PrimaryKey primaryKey) {
        List<ColumnInfo> keys = primaryKey.getKeys();
        List<String> conditionClauseList = new ArrayList<>(keys.size());
        for(ColumnInfo column : keys) {
            conditionClauseList.add(getAssignmentClause(column));
        }

        return "WHERE " + StringUtils.join(" AND ", conditionClauseList);
    }

    protected String getPrimaryWhereClause(Collection<String> columns) {
        List<String> conditionClauseList = new ArrayList<>(columns.size());
        int i = 0;
        for(String columnName : columns) {
            conditionClauseList.add(columnName + " = #{p" + i + "}");
            i++;
        }

        return "WHERE " + StringUtils.join(" AND ", conditionClauseList);
    }

    protected String getAssignmentClause(ColumnInfo column) {
        String formatter = "{0} = {1}";
        String parameterText = getSQLParameter(column);
        return StringUtils.format(formatter, column.getColumnName(), parameterText);
    }

    protected String getConditionClause(String test, String inner) {
        return StringUtils.format("<if test=\"{0}\">{1}</if>", test, inner) ;
    }

    //#endregion
}
