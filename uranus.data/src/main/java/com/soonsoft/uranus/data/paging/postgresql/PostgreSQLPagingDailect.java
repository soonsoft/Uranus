package com.soonsoft.uranus.data.paging.postgresql;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.paging.IPagingDailect;

/**
 * PostgreSql 分页SQL语句转换
 */
public class PostgreSQLPagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        Guard.notEmpty(commandText, "the commandText is required.");

        StringBuilder limitSql = new StringBuilder(commandText);
        limitSql.append(" OFFSET ").append(offset).append(" LIMIT ").append(limit);
        return limitSql.toString();
    }

    @Override
    public String buildCountingSql(String commandText) {
        Guard.notEmpty(commandText, "the commandText is required.");
        
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS PAGING_TEMP_TABLE", sql);
    }

    
}
