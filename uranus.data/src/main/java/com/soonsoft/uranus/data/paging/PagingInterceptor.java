package com.soonsoft.uranus.data.paging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

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

@Intercepts({
    @Signature(
        type = Executor.class,
        method = "query", 
        args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
public class PagingInterceptor implements Interceptor {

    private final static String COUNTING_SUFFIX = "-SELECT-COUNT";
    private final static List<Object> EMPTY_QUERY_RESULT = new EmptyDataSet();

    private IPagingDailect pagingDailect;

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

            if(pageRowBounds.getTotal() == 0) {
                return EMPTY_QUERY_RESULT;
            }
        }

        String pagingSql = this.pagingDailect.buildPagingSql(originalSql.getSql(), pageRowBounds.getOffset(), pageRowBounds.getLimit());
        BoundSql pagingBoundSql = new BoundSql(statement.getConfiguration(), pagingSql, 
            originalSql.getParameterMappings(), originalSql.getParameterObject());
        MappedStatement pagingStatement = rebuildMappedStatement(statement, pagingBoundSql, statement.getId(), null);

        // 替换掉参数
        args[0] = pagingStatement;
        // 替换分页参数，避免触发mybatis自己的逻辑分页逻辑，否则只有第一页能返回数据
        args[2] = RowBounds.DEFAULT;
        
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

    public static class EmptyDataSet extends ArrayList<Object> {

        public EmptyDataSet() {
            super(0);
        }

        @Override
        public boolean add(Object e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean addAll(Collection<? extends Object> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends Object> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(Predicate<? super Object> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
        
    }
}