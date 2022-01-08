package com.soonsoft.uranus.data.service.jdbc;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.DatabaseAccessException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcTemplateDatabaseAccess extends BaseDatabaseAccess<NamedParameterJdbcTemplate> {

    private IPagingDailect pagingDailect;

    public IPagingDailect getPagingDailect() {
        return pagingDailect;
    }

    public void setPagingDailect(IPagingDailect pagingDailect) {
        this.pagingDailect = pagingDailect;
    }
    
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
    public <T> T get(String commandText) {
        return get(commandText, null);
    }

    @Override
    public <T> T get(String commandText, Object parameter) {
        List<T> list;
        if(parameter == null) {
            list = select(commandText);
        } else {
            Map<String, Object> params = null;
            list = select(commandText, params);
        }
        if(list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        if(size == 1) {
            return list.get(0);
        }
        throw new DatabaseAccessException("Expected one result (or null) to be returned by get(), but found: " + size);
    }

    @Override
    public <T> List<T> select(String commandText) {
        return querySQL(commandText, null, null);
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params) {
        return querySQL(commandText, params, null);
    }

    @Override
    public <T> List<T> select(String commandText, Map<String, Object> params, Page page) {
        return querySQL(commandText, params, page);
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
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.updateSQL and error has occurred.", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> querySQL(String sql, Map<String, Object> params, Page page) {
        Guard.notEmpty(sql, "the parameter sql is required.");

        Class<?> resultType = null;

        if(params instanceof JdbcSqlParameter p) {
            resultType = p.getResultType();
        }

        if(resultType == null) {
            resultType = Map.class;
        }

        try {
            String querySql = sql;
            if(page != null) {
                if(pagingDailect == null) {
                    throw new DatabaseAccessException("the pagingDailect is null.");
                }

                String countingSql = pagingDailect.buildCountingSql(sql);
                sql = pagingDailect.buildPagingSql(sql, page.offset(), page.limit());

                Integer count = ensureGetTemplate().query(countingSql, params, rs -> {
                    return rs.getInt(0);
                });
                page.setTotal(count.intValue());
            }

            List<T> result = (List<T>) ensureGetTemplate().queryForList(querySql, params, resultType);
            return result;

        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.querySQL and error has occurred.", e);
        }
    }
    
}
