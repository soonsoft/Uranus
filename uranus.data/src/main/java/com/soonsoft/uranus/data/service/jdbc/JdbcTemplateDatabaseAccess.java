package com.soonsoft.uranus.data.service.jdbc;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.DatabaseAccessException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcTemplateDatabaseAccess extends BaseDatabaseAccess<NamedParameterJdbcTemplate> {
    
    //#region IDatabaseAccess implements

    @Override
    public int insert(String commandText) {
        try {
            return ensureGetTemplate().getJdbcTemplate().update(commandText);
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.insert and error has occurred.", e);
        }
    }

    @Override
    public int insert(String commandText, Object parameter) {
        return updateSQL(commandText, parameter);
    }

    @Override
    public int update(String commandText) {
        try {
            return ensureGetTemplate().getJdbcTemplate().update(commandText);
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.update and error has occurred.", e);
        }
    }

    @Override
    public int update(String commandText, Object parameter) {
        return updateSQL(commandText, parameter);
    }

    @Override
    public int delete(String commandText) {
        try {
            return ensureGetTemplate().getJdbcTemplate().update(commandText);
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.delete and error has occurred.", e);
        }
    }

    @Override
    public int delete(String commandText, Object parameter) {
        return updateSQL(commandText, parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String commandText) {
        Object result = selectOne(commandText, Map.class);
        return (T) result;
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

    
    @SuppressWarnings("unchecked")
    protected int updateSQL(String sql, Object parameter) {
        try {
            if(parameter == null) {
                return ensureGetTemplate().getJdbcTemplate().update(sql);
            }
            
            if(parameter instanceof Map) {
                return ensureGetTemplate().update(sql, (Map<String, Object>) parameter);
            }

            if(parameter instanceof Object[]) {
                return ensureGetTemplate().getJdbcTemplate().update(sql, (Object[]) parameter);
            }

            return 0;
            
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.insert and error has occurred.", e);
        }
    }

    private <T> T selectOne(String sql, Class<T> resultType) {
        try {
            List<T> result = ensureGetTemplate().getJdbcTemplate().queryForList(sql, resultType);
            if(result != null && !result.isEmpty()) {
                return (T) result.get(0);
            }
            return null;
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.delete and error has occurred.", e);
        }
    }
    
}
