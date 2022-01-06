package com.soonsoft.uranus.data.paging.sqlite;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.paging.IPagingDailect;

public class SQLitePagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        Guard.notEmpty(commandText, "the commandText is required.");

        return String.format(
            "SELECT * FROM (%s) PAGING_TEMP_TABLE LIMIT %s OFFSET %s", 
            commandText, limit, offset);
    }

    @Override
    public String buildCountingSql(String commandText) {
        Guard.notEmpty(commandText, "the commandText is required.");
        
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS PAGING_TEMP_TABLE", sql);
    }
    
}
