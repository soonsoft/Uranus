package com.soonsoft.uranus.data.service.mybatis;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.interceptor.PagingRowBounds;
import com.soonsoft.uranus.data.service.mybatis.mapper.MappedStatementRegistry;
import com.soonsoft.uranus.core.Guard;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;


public class MybatisDatabaseAccess extends BaseDatabaseAccess<SqlSessionTemplate> {

    private MappedStatementRegistry mappedStatementRegistry;
    
    public MappedStatementRegistry getMappedStatementRegistry() {
        return mappedStatementRegistry;
    }

    public void setMappedStatementRegistry(MappedStatementRegistry mappedStatementRegistry) {
        this.mappedStatementRegistry = mappedStatementRegistry;
    }


    //#region IDatabaseAccess implements

    @Override
    public int insert(String commandText) {
        return ensureGetTemplate().insert(commandText);
    }

    @Override
    public int insert(String commandText, Object parameter) {
        return ensureGetTemplate().insert(commandText, parameter);
    }

    @Override
    public int[] insertBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
    }

    @Override
    public int update(String commandText) {
        return ensureGetTemplate().update(commandText);
    }

    @Override
    public int update(String commandText, Object parameter) {
        return ensureGetTemplate().update(commandText, parameter);
    }

    @Override
    public int[] updateBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
    }

    @Override
    public int delete(String commandText) {
        return ensureGetTemplate().delete(commandText);
    }

    @Override
    public int delete(String commandText, Object parameter) {
        return ensureGetTemplate().delete(commandText, parameter);
    }

    @Override
    public int[] deleteBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
    }

    @Override
    public <T> T get(String commandText) {
        return ensureGetTemplate().selectOne(commandText);
    }

    @Override
    public <T> T get(String commandText, Object parameter) {
        return ensureGetTemplate().selectOne(commandText, parameter);
    }

    @Override
    public <T> List<T> select(String commandText) {
        return ensureGetTemplate().selectList(commandText);
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params) {
        return ensureGetTemplate().selectList(commandText, params);
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        Guard.notNull(page, "the Page is required.");

        PagingRowBounds rowBounds = new PagingRowBounds(page.offset(), page.limit());
        List<T> result = ensureGetTemplate().selectList(commandText, params, rowBounds);
        page.setTotal(rowBounds.getTotal());
        return result;
    }

    //#endregion

    protected int[] updateBatchSQL(String sql, Object[] parameters) {
        Guard.notEmpty(sql, "the parameter sql is required.");
        Guard.notEmpty(parameters, "the parameter parameters is required.");

        int[] result = new int[parameters.length];

        SqlSessionFactory sessionFactory = getTemplate().getSqlSessionFactory();
        try(SqlSession session = sessionFactory.openSession(ExecutorType.BATCH)) {
            for(int i = 0; i < parameters.length; i++) {
                result[i] = session.insert(sql, parameters[i]);
            }

            session.commit();
        }
        return result;
    }
    
}