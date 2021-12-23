package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.service.mate.ColumnInfo;
import com.soonsoft.uranus.data.service.mate.PrimaryKey;
import com.soonsoft.uranus.data.service.mate.TableInfo;
import com.soonsoft.uranus.data.service.mybatis.mapper.BaseSQLMapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class GetByPrimary extends BaseSQLMapper {

    public GetByPrimary() {
        super("getByPrimary");
    }

    @Override
    protected MappedStatement add(Class<?> entityClass, TableInfo tableInfo) {
        Class<?> parameterType = java.util.HashMap.class;
        String sql = createSelectSQL(tableInfo);
        SqlSource sqlSource = createSqlSource(sql, parameterType);
        
        return addSelectMappedStatement(getMapperName(), sqlSource, parameterType, entityClass);
    }

    private String createSelectSQL(TableInfo tableInfo) {
        String sql = "SELECT {0} FROM {1} {2}";
        List<String> columnNames = new ArrayList<>(tableInfo.getColumns().size());
        for(ColumnInfo column : tableInfo.getColumns()) {
            columnNames.add(StringUtils.format("{0} AS {1}", column.getColumnName(), column.getEntityFieldName()));
        }

        PrimaryKey primaryKey = tableInfo.getPrimaryKey();
        List<String> whereColumnNames = new ArrayList<>(primaryKey.getKeys().size());
        for(ColumnInfo column : primaryKey.getKeys()) {
            whereColumnNames.add(column.getColumnName());
        }

        return StringUtils.format(sql, 
            StringUtils.join(",", columnNames),
            tableInfo.getTableName(), 
            getPrimaryWhereClause(whereColumnNames));
    }
    
}
