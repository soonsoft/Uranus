package com.soonsoft.uranus.data.paging.postgresql;

import com.soonsoft.uranus.data.paging.IPagingDailect;

/**
 * PostgreSqlPagingDailect
 */
public class PostgreSQLPagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        StringBuilder limitSql = new StringBuilder(commandText);
        limitSql.append(" OFFSET ").append(offset).append(" LIMIT ").append(limit);
        return limitSql.toString();
    }

    @Override
    public String buildCountingSql(String commandText) {
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS TEMP", sql);
    }

    
}