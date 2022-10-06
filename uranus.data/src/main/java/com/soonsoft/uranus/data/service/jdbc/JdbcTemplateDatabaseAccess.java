package com.soonsoft.uranus.data.service.jdbc;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.data.service.BaseDatabaseAccess;
import com.soonsoft.uranus.data.service.DatabaseAccessException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
    public int[] insertBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
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
    public int[] updateBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
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
    public int[] deleteBatch(String commandText, Object... parameters) {
        return updateBatchSQL(commandText, parameters);
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
        Guard.notEmpty(sql, "the parameter sql is required.");

        try {
            if(parameter == null) {
                return ensureGetTemplate().getJdbcTemplate().update(sql);
            }
            
            if(parameter instanceof Map) {
                return ensureGetTemplate().update(sql, (Map<String, Object>) parameter);
            }

            if(parameter instanceof SqlParameterSource paramSource) {
                return ensureGetTemplate().update(sql, paramSource);
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
    protected int[] updateBatchSQL(String sql, Object[] parameters) {
        Guard.notEmpty(sql, "the parameter sql is required.");
        Guard.notEmpty(parameters, "the parameter parameters is required.");

        try {

            Object element = parameters[0];
            
            if(element instanceof Map) {
                return ensureGetTemplate().batchUpdate(sql, (Map<String, ?>[]) parameters);
            }

            if(element instanceof SqlParameterSource paramSource) {
                return ensureGetTemplate().batchUpdate(sql, (SqlParameterSource[]) parameters);
            }

            return new int[0];
        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.updateBatchSQL and error has occurred.", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> querySQL(String sql, Map<String, Object> params, Page page) {
        Guard.notEmpty(sql, "the parameter sql is required.");

        try {
            String querySql = sql;
            NamedParameterJdbcTemplate jdbcTemplate = ensureGetTemplate();
            if(page != null) {
                if(pagingDailect == null) {
                    throw new DatabaseAccessException("the pagingDailect is null.");
                }

                String countingSql = pagingDailect.buildCountingSql(sql);
                querySql = pagingDailect.buildPagingSql(sql, page.offset(), page.limit());

                Integer count = jdbcTemplate.queryForObject(countingSql, params, (rs, rowNum) -> {
                    return rs.getInt(1);
                });
                page.setTotal(count == null ? 0 : count.intValue());
            }

            RowMapper<?> rowMapper = null;
            if(params instanceof JdbcSqlParameter<?> p) {
                rowMapper = p.getRowMapper();
            }

            List<T> result = null;
            if(rowMapper == null) {
                // result List<Map<String, Object>>
                result = (List<T>) jdbcTemplate.queryForList(querySql, params);
            } else {
                if(params.isEmpty()) {
                    result = (List<T>) jdbcTemplate.query(querySql, rowMapper);
                } else {
                    result = (List<T>) jdbcTemplate.query(querySql, params, rowMapper);
                }
            }
            return result;

        } catch(DataAccessException e) {
            throw new DatabaseAccessException("JdbcTemplateDatabaseAccess.querySQL and error has occurred.", e);
        }
    }
    
}
