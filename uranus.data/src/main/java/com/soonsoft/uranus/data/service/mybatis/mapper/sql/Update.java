package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.mate.ColumnInfo;
import com.soonsoft.uranus.data.service.mate.TableInfo;
import com.soonsoft.uranus.data.service.mybatis.mapper.BaseSQLMapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class Update extends BaseSQLMapper {

    public Update() {
        super("update");
    }

    protected Update(String name) {
        super(name);
    }

    @Override
    protected MappedStatement add(Class<?> entityClass, TableInfo tableInfo) {
        String sql = createUpdateSQL(tableInfo);
        SqlSource sqlSource = createSqlSource(sql, entityClass);

        return addUpdateMappedStatement(getMapperName(), sqlSource, entityClass);
    }

    protected String createUpdateSQL(TableInfo tableInfo) {
        String sql = "UPDATE {0} SET {1} {2}";
        List<ColumnInfo> columns = tableInfo.getColumns();
        List<String> setClause = new ArrayList<>(columns.size());
        for(ColumnInfo column : columns) {
            if(column.isPrimaryKey()) {
                continue;
            }
            setClause.add(getAssignmentClause(column));
        }
        return StringUtils.format(sql, 
            tableInfo.getTableName(), 
            StringUtils.join(",", setClause),
            getPrimaryWhereClause(tableInfo.getPrimaryKey()));
    }
    
}
