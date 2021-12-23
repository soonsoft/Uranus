package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.mate.ColumnInfo;
import com.soonsoft.uranus.data.service.mate.IDType;
import com.soonsoft.uranus.data.service.mate.TableInfo;

public class InsertSelective extends Insert {

    public InsertSelective() {
        super("insertSelective", null);
    }

    @Override
    protected String createInsertSQL(TableInfo tableInfo) {
        boolean isAutoKey = tableInfo.getPrimaryIdType() == IDType.AUTO;

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(tableInfo.getTableName());

        sqlBuilder.append(" ");
        sqlBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for(ColumnInfo column : tableInfo.getColumns()) {
            if(isAutoKey && column.isPrimaryKey()) {
                continue;
            }
            sqlBuilder.append(
                getConditionClause(
                    StringUtils.format("{0} != null", column.getEntityFieldName()), 
                    column.getColumnName() + ",")
            );
        }
        sqlBuilder.append("</trim>");

        sqlBuilder.append(" ");
        sqlBuilder.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        for(ColumnInfo column : tableInfo.getColumns()) {
            if(isAutoKey && column.isPrimaryKey()) {
                continue;
            }
            sqlBuilder.append(
                getConditionClause(
                    StringUtils.format("{0} != null", column.getEntityFieldName()), 
                    getSQLParameter(column) + ",")
            );
        }
        sqlBuilder.append("</trim>");

        return sqlBuilder.toString();
    }
    
}
