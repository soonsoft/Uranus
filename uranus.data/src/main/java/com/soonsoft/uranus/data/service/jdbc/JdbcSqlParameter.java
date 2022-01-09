package com.soonsoft.uranus.data.service.jdbc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public class JdbcSqlParameter<R> extends LinkedHashMap<String, Object> {

    private Class<?> resultType;

    private RowMapper<R> rowMapper;

    public JdbcSqlParameter() {
        
    }

    public JdbcSqlParameter(Map<String, ?> params) {
        if(params != null) {
            putAll(params);
        }
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    public RowMapper<R> getRowMapper() {
        return rowMapper;
    }

    public void setRowMapper(RowMapper<R> rowMapper) {
        this.rowMapper = rowMapper;
    }

    
    
}
