package com.soonsoft.uranus.data.service.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.DatabaseAccessException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcTemplateDatabaseAccess extends BaseDatabaseAccess<NamedParameterJdbcTemplate> {

    private final static Map<String, Object> EMPTY_PARAM = new HashMap<>(0);
    
    //#region IDatabaseAccess implements

    @Override
    public int insert(String commandText) {
        try {
            return ensureGetTemplate().update(commandText, EMPTY_PARAM);
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.insert and error has occurred.", e);
        }
    }

    @Override
    @SuppressWarnings(value = { "unchecked" })
    public int insert(String commandText, Object parameter) {
        try {
            if(parameter == null) {
                return ensureGetTemplate().update(commandText, EMPTY_PARAM);
            }
            if(parameter instanceof Map) {
                return ensureGetTemplate().update(commandText, (Map<String, Object>) parameter);
            }

            return 0;
            
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.insert and error has occurred.", e);
        }
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

    //#endregion
    
}
