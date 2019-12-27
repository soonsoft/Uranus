package com.soonsoft.uranus.data.paging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.soonsoft.uranus.data.IDailect;
import com.soonsoft.uranus.core.common.lang.StringUtils;

/**
 * 分页SQL方言
 */
public interface IPagingDailect extends IDailect {

    final static Pattern ORDER_BY = Pattern.compile("\\border\\s+by\\b", Pattern.CASE_INSENSITIVE);

    String buildPagingSql(String commandText, int offset, int limit);

    String buildCountingSql(String commandText);

    public static String removeOrderClause(String sql) {
        if(StringUtils.isEmpty(sql)) {
            return sql;
        }

        Matcher matcher = ORDER_BY.matcher(sql);
        int index = -1;
        int startIndex = 0;
        while(matcher.find(startIndex)) {
            index = matcher.start();
            startIndex = matcher.end();
        }
        return sql.substring(0, index);
    }
}