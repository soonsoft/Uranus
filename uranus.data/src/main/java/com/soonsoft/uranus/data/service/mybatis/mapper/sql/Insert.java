package com.soonsoft.uranus.data.service.mybatis.mapper.sql;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.data.service.meta.ColumnInfo;
import com.soonsoft.uranus.data.service.meta.IDType;
import com.soonsoft.uranus.data.service.meta.PrimaryKey;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.mybatis.mapper.BaseSQLMapper;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class Insert extends BaseSQLMapper {

    private final Func1<PrimaryKey, KeyGenerator> keyGeneratorFactory;

    public Insert() {
        this(null);
    }

    public Insert(Func1<PrimaryKey, KeyGenerator> keyGeneratorFactory) {
        super("insert");
        this.keyGeneratorFactory = keyGeneratorFactory;
    }

    protected Insert(String name, Func1<PrimaryKey, KeyGenerator> keyGeneratorFactory) {
        super(name);
        this.keyGeneratorFactory = keyGeneratorFactory;
    }

    @Override
    protected MappedStatement add(Class<?> entityClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = 
            tableInfo.getPrimaryIdType() == IDType.AUTO 
                ? Jdbc3KeyGenerator.INSTANCE 
                : NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        PrimaryKey primaryKey = tableInfo.getPrimaryKey();
        if(primaryKey != null && !primaryKey.isComposite()) {
            ColumnInfo primaryColumn = primaryKey.getKeys().stream().findFirst().orElse(null);
            if(keyGeneratorFactory != null) {
                keyGenerator = keyGeneratorFactory.call(primaryKey);
            }
            keyProperty = primaryColumn.getEntityFieldName();
            keyColumn = primaryColumn.getColumnName();
        }
        
        String sql = createInsertSQL(tableInfo);
        SqlSource sqlSource = createSqlSource(sql, entityClass);

        return addInsertMappedStatement(getMapperName(), sqlSource, entityClass, keyGenerator, keyProperty, keyColumn);
    }

    protected String createInsertSQL(TableInfo tableInfo) {
        String sql = "INSERT INTO {0}({1}) VALUES({2})";
        String tableName = tableInfo.getTableName();
        int size = tableInfo.getColumns().size();
        List<String> columns = new ArrayList<>(size);
        List<String> values = new ArrayList<>(size);

        for(ColumnInfo column : tableInfo.getColumns()) {
            if(tableInfo.getPrimaryIdType() == IDType.AUTO && column.isPrimaryKey()) {
                continue;
            }
            columns.add(column.getColumnName());
            values.add(getSQLParameter(column));
        }

        return StringUtils.format(sql, 
            tableName, StringUtils.join(",", columns), StringUtils.join(",", values));
    }
    
}
