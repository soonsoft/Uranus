package com.soonsoft.uranus.data.service.jdbc;

import java.util.LinkedHashMap;

public class JdbcSqlParameter extends LinkedHashMap<String, Object> {

    private final Class<?> resultType;

    public JdbcSqlParameter(Class<?> resultType) {
        this.resultType = resultType;
    }

    public Class<?> getResultType() {
        return this.resultType;
    }
    
}
