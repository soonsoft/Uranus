package com.soonsoft.uranus.data.paging.sqlserver;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.paging.IPagingDailect;

public class SqlServerPagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        Guard.notEmpty(commandText, "the commandText is required.");

        return String.format(
            "SELECT * FROM (%s) PAGING_TEMP_TABLE OFFSET %s ROW FETCH NEXT %s ROWS ONLY", 
            commandText, offset, limit);
    }

    @Override
    public String buildCountingSql(String commandText) {
        Guard.notEmpty(commandText, "the commandText is required.");
        
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS PAGING_TEMP_TABLE", sql);
    }
    
}
