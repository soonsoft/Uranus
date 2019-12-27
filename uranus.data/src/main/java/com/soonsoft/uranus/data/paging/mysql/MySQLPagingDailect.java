package com.soonsoft.uranus.data.paging.mysql;

import com.soonsoft.uranus.data.paging.IPagingDailect;
import com.soonsoft.uranus.core.Guard;

/**
 * MySQLPagingDailect
 */
public class MySQLPagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        Guard.notEmpty(commandText, "the commandText is required.");

        StringBuilder pagingSql = new StringBuilder(commandText);
        pagingSql.append(" LIMIT ").append(offset).append(",").append(limit);
        return pagingSql.toString();
    }

    @Override
    public String buildCountingSql(String commandText) {
        Guard.notEmpty(commandText, "the commandText is required.");
        
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS TEMP", sql);
    }

    
}