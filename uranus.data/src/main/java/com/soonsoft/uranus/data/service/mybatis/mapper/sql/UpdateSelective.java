package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.meta.ColumnInfo;
import com.soonsoft.uranus.data.service.meta.TableInfo;

public class UpdateSelective extends Update {

    public UpdateSelective() {
        super("updateSelective");
    }

    @Override
    protected String createUpdateSQL(TableInfo tableInfo) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(tableInfo.getTableName());

        sqlBuilder.append(" <set>");
        for(ColumnInfo column : tableInfo.getColumns()) {
            if(column.isPrimaryKey()) {
                continue;
            }
            sqlBuilder.append(
                getConditionClause(
                    StringUtils.format("{0} != null", column.getEntityFieldName()), 
                    getAssignmentClause(column))
            );
        }
        sqlBuilder.append("</set>");

        sqlBuilder.append(" ").append(getPrimaryWhereClause(tableInfo.getPrimaryKey()));

        return sqlBuilder.toString();
    }
    
}
