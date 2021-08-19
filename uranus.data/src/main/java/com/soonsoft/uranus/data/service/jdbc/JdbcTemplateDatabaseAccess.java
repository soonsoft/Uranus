package com.soonsoft.uranus.data.service.jdbc;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;

import org.springframework.jdbc.core.JdbcTemplate;

// TODO 实现 JdbcTemplateDatabaseAccess
public class JdbcTemplateDatabaseAccess extends BaseDatabaseAccess<JdbcTemplate> {


    @Override
    public int insert(String commandText) {
        return ensureGetTemplate().update(commandText);
    }

    @Override
    public int insert(String commandText, Object parameter) {
        return 0;
    }

    @Override
    public int update(String commandText) {
        return 0;
    }

    @Override
    public int update(String commandText, Object parameter) {
        return 0;
    }

    @Override
    public int delete(String commandText) {
        return 0;
    }

    @Override
    public int delete(String commandText, Object parameter) {
        return 0;
    }

    @Override
    public <T> T get(String commandText) {
        return null;
    }

    @Override
    public <T> T get(String commandText, Object parameter) {
        return null;
    }

    @Override
    public <T> List<T> select(String commandText) {
        return null;
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params) {
        return null;
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        return null;
    }
    
}
