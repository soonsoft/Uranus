package com.soonsoft.uranus.data.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.soonsoft.uranus.data.service.mybatis.PageRowBounds;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * PagingInterceptor
 */
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query", 
        args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
public class PagingInterceptor implements Interceptor {

    private IPagingDailect pagingDailect;

    private static String COUNTING_SUFFIX = "-SELECT-COUNT";

    public PagingInterceptor() {

    }

    public PagingInterceptor(IPagingDailect pagingDailect) {
        this.pagingDailect = pagingDailect;
    }

    public IPagingDailect getPagingDailect() {
        return pagingDailect;
    }

    public void setPagingDailect(IPagingDailect pagingDailect) {
        this.pagingDailect = pagingDailect;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];

        if(!(rowBounds instanceof PageRowBounds)) {
            return invocation.proceed();
        }

        BoundSql originalSql = statement.getBoundSql(parameter);
        String countingSql = this.pagingDailect.buildCountingSql(originalSql.getSql());
        PageRowBounds pageRowBounds = (PageRowBounds) rowBounds;
        if(!StringUtils.isEmpty(countingSql)) {
            String statementId = statement.getId() + COUNTING_SUFFIX;
            ResultMap countingResultMap = 
                new ResultMap.Builder(
                    statement.getConfiguration(), 
                    statementId, Integer.class,
                    Collections.<ResultMapping>emptyList())
                .build();
            BoundSql countingBoundSql = new BoundSql(statement.getConfiguration(), countingSql,
                originalSql.getParameterMappings(), originalSql.getParameterObject());

            
            MappedStatement countingStatement = rebuildMappedStatement(statement, countingBoundSql, statementId, countingResultMap);
            Executor executor = (Executor) invocation.getTarget();
            List<Integer> result = executor.query(countingStatement, parameter, RowBounds.DEFAULT, null);
            if(result != null && !result.isEmpty()) {
                pageRowBounds.setTotal(result.get(0).intValue());
            }
        }

        String pagingSql = this.pagingDailect.buildPagingSql(originalSql.getSql(), pageRowBounds.getOffset(), pageRowBounds.getLimit());
        BoundSql pagingBoundSql = new BoundSql(statement.getConfiguration(), pagingSql, 
            originalSql.getParameterMappings(), originalSql.getParameterObject());
        MappedStatement pagingStatement = rebuildMappedStatement(statement, pagingBoundSql, statement.getId(), null);
        args[0] = pagingStatement;
        
        return invocation.proceed();
    }

    private MappedStatement rebuildMappedStatement(MappedStatement statement, BoundSql newBoundSql, String id, ResultMap resultMap){
        
        StaticSqlSource sqlSource = new StaticSqlSource(
            statement.getConfiguration(), newBoundSql.getSql(), newBoundSql.getParameterMappings());
        MappedStatement.Builder builder = new MappedStatement.Builder(
            statement.getConfiguration(), id, sqlSource, statement.getSqlCommandType());

        builder.resource(statement.getResource());
        builder.fetchSize(statement.getFetchSize());
        builder.statementType(statement.getStatementType());
        builder.keyGenerator(statement.getKeyGenerator());
        builder.timeout(statement.getTimeout());
        builder.parameterMap(statement.getParameterMap());

        builder.resultSetType(statement.getResultSetType());
        builder.cache(statement.getCache());
        builder.flushCacheRequired(statement.isFlushCacheRequired());
        builder.useCache(statement.isUseCache());
        builder.resultMaps(statement.getResultMaps());
        if (resultMap != null) {
            List<ResultMap> resultMaps = new ArrayList<>(1);
            resultMaps.add(resultMap);
            builder.resultMaps(resultMaps);
        }

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        if(target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // 用于设置外部的配置信息
    }
}