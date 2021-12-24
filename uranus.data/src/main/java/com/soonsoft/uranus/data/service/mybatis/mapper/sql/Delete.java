package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.meta.ColumnInfo;
import com.soonsoft.uranus.data.service.meta.PrimaryKey;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.mybatis.mapper.BaseSQLMapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class Delete extends BaseSQLMapper {

    public Delete() {
        super("delete");
    }

    @Override
    protected MappedStatement add(Class<?> entityClass, TableInfo tableInfo) {
        PrimaryKey primaryKey = tableInfo.getPrimaryKey();
        Class<?> parameterType = java.util.HashMap.class;
        List<String> columnNames = new ArrayList<>(primaryKey.getKeys().size());
        for(ColumnInfo column : primaryKey.getKeys()) {
            columnNames.add(column.getColumnName());
        }

        String sql = StringUtils.format("DELETE FROM {0} {1}", 
            tableInfo.getTableName(), getPrimaryWhereClause(primaryKey));
        SqlSource sqlSource = createSqlSource(sql, parameterType);

        return addDeleteMappedStatement(getMapperName(), sqlSource, parameterType);
    }
    
}
