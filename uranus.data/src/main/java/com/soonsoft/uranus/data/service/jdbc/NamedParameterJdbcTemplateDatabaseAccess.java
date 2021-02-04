package com.soonsoft.uranus.data.service.jdbc;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class NamedParameterJdbcTemplateDatabaseAccess extends BaseDatabaseAccess<NamedParameterJdbcTemplate> {

    @Override
    public int insert(String commandText) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int insert(String commandText, Object parameter) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(String commandText) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(String commandText, Object parameter) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(String commandText) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(String commandText, Object parameter) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> T get(String commandText) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(String commandText, Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> select(String commandText) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
