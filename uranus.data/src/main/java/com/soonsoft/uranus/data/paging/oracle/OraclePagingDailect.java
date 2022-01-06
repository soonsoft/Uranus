package com.soonsoft.uranus.data.paging.oracle;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.data.paging.IPagingDailect;

/*

[old-SQL]
parameter: startRowNum 1开始，endRowNum 结束行；
SELECT * FROM 
    (SELECT 
        PAGING_TEMP_TABLE0.*, ROWNUM AS PAGING_ROW_NUMBER 
        FROM (SELECT * FROM table1) PAGING_TEMP_TABLE0
        WHERE ROWNUM <= :endRowNum) PAGING_TEMP_TABLE1
    WHERE PAGING_ROW_NUMBER >= :startRowNum

[12c-SQL]
parameter: offset 0开始，limit 取几行；
SELECT * FROM table1 OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY

*/


public class OraclePagingDailect implements IPagingDailect {

    @Override
    public String buildPagingSql(String commandText, int offset, int limit) {
        Guard.notEmpty(commandText, "the commandText is required.");

        int rownumBegin = offset + 1;
        int rownumEnd = offset + limit;
        return """
        SELECT * FROM 
            (SELECT 
                PAGING_TEMP_TABLE0.*, ROWNUM AS PAGING_ROW_NUMBER 
                FROM (%s) PAGING_TEMP_TABLE0
                WHERE ROWNUM <= %s) PAGING_TEMP_TABLE1
            WHERE PAGING_ROW_NUMBER >= %s 
        """.formatted(commandText, rownumEnd, rownumBegin);
    }

    @Override
    public String buildCountingSql(String commandText) {
        Guard.notEmpty(commandText, "the commandText is required.");
        
        String sql = IPagingDailect.removeOrderClause(commandText);
        return String.format("SELECT COUNT(*) AS Total FROM(%s) AS PAGING_TEMP_TABLE", sql);
    }
    
}
