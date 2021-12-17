package com.soonsoft.uranus.data.service.mybatis;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.interceptor.PagingRowBounds;
import com.soonsoft.uranus.core.Guard;

import org.mybatis.spring.SqlSessionTemplate;


public class MybatisDatabaseAccess extends BaseDatabaseAccess<SqlSessionTemplate> {
    
    @Override
    public int insert(String commandText) {
        return ensureGetTemplate().insert(commandText);
    }

    @Override
    public int insert(String commandText, Object parameter) {
        return ensureGetTemplate().insert(commandText, parameter);
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
    public int delete(String commandText) {
        return ensureGetTemplate().delete(commandText);
    }

    @Override
    public int delete(String commandText, Object parameter) {
        return ensureGetTemplate().delete(commandText, parameter);
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
    @SuppressWarnings(value = "unchecked")
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        Guard.notNull(page, "the Page is required.");

        PagingRowBounds rowBounds = new PagingRowBounds(page.offset(), page.limit());
        List<Object> result = ensureGetTemplate().selectList(commandText, params, rowBounds);
        page.setTotal(rowBounds.getTotal());
        return (List<T>) result;
    }
    
}