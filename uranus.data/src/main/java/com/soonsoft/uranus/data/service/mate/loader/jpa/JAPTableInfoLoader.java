package com.soonsoft.uranus.data.service.mate.loader.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.soonsoft.uranus.data.service.mate.ColumnInfo;
import com.soonsoft.uranus.data.service.mate.IDType;
import com.soonsoft.uranus.data.service.mate.PrimaryKey;
import com.soonsoft.uranus.data.service.mate.TableInfo;
import com.soonsoft.uranus.data.service.mate.anno.MappingType;
import com.soonsoft.uranus.data.service.mate.loader.ITableInfoLoader;

public class JAPTableInfoLoader implements ITableInfoLoader {

    private final static Map<Class<?>, JDBCType> JDBC_TYPE_MAPPER = new HashMap<>() {
        {
            put(Byte.class, JDBCType.TINYINT);
            put(Short.class, JDBCType.SMALLINT);
            put(Integer.class, JDBCType.INTEGER);
            put(Long.class, JDBCType.BIGINT);
            put(Float.class, JDBCType.FLOAT);
            put(Double.class, JDBCType.DOUBLE);
            put(BigDecimal.class, JDBCType.DECIMAL);
            put(String.class, JDBCType.VARCHAR);
            put(java.util.Date.class, JDBCType.TIMESTAMP);
            put(java.time.LocalDate.class, JDBCType.DATE);
            put(java.time.LocalTime.class, JDBCType.TIME);
            put(java.sql.Date.class, JDBCType.DATE);
            put(java.sql.Time.class, JDBCType.TIME);
            put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
            put(java.util.UUID.class, JDBCType.VARCHAR);
            put(byte[].class, JDBCType.BLOB);
            put(Boolean.class, JDBCType.BIT);
            put(Character.class, JDBCType.CHAR);
            put(Number.class, JDBCType.NUMERIC);
            put(Void.class, JDBCType.NULL);
        }
    };

    @Override
    public TableInfo load(Class<?> entityClass) {
        TableInfo tableInfo = new TableInfo();
        
        Table table = entityClass.getAnnotation(Table.class);
        if(table == null) {
            throw new IllegalArgumentException("the [" + entityClass.getName() + "] not contain @Table.");
        }
        tableInfo.setTableName(table.name());
        tableInfo.setEntityType(entityClass);
        tableInfo.setColumns(new ArrayList<>());

        Field[] fields = entityClass.getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            loadColumnInfo(field, tableInfo);
        }

        List<ColumnInfo> keys = 
            tableInfo.getColumns().stream()
                .filter(c -> c.isPrimaryKey())
                .collect(Collectors.toList());
        tableInfo.setPrimaryKey(new PrimaryKey(keys));

        return tableInfo;
    }

    protected void loadColumnInfo(Field field, TableInfo tableInfo) {
        Annotation[] annotations = field.getAnnotations();

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setEntityFieldName(field.getName());
        columnInfo.setEntityFieldType(field.getType());

        if(columnInfo != null) {
            for(int i = 0; i < annotations.length; i++) {
                Annotation annotationInstance = annotations[i];
                
                if(annotationInstance instanceof Column column) {
                    columnInfo.setColumnName(column.name());
                    columnInfo.setNotNull(!column.nullable());
                } else if(annotationInstance instanceof Id) {
                    columnInfo.setPrimaryKey(true);
                } else if(annotationInstance instanceof GeneratedValue generatedValue) {
                    switch(generatedValue.strategy()) {
                        case AUTO : tableInfo.setPrimaryIdType(IDType.AUTO); break;
                        default: tableInfo.setPrimaryIdType(IDType.NONE);
                    }
                } else if(annotationInstance instanceof MappingType type) {
                    columnInfo.setColumnType(type.value());
                }
            }
        }

        if(columnInfo.getColumnType() == null) {
            columnInfo.setColumnType(JDBC_TYPE_MAPPER.get(field.getType()));
        }

        if(columnInfo.getColumnName() != null) {
            tableInfo.getColumns().add(columnInfo);
        }
    }
    
}
