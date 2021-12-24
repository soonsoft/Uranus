package com.soonsoft.uranus.data.service.meta.loader.jpa;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.data.service.meta.ColumnInfo;
import com.soonsoft.uranus.data.service.meta.IDType;
import com.soonsoft.uranus.data.service.meta.PrimaryKey;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.meta.anno.MappingType;
import com.soonsoft.uranus.data.service.meta.loader.ITableInfoLoader;

public class JAPTableInfoLoader implements ITableInfoLoader {

    private final static Map<Class<?>, JDBCType> JDBC_TYPE_MAPPER = new HashMap<>() {
        {
            put(Boolean.TYPE, JDBCType.BIT);
            put(Character.TYPE, JDBCType.CHAR);
            put(Byte.TYPE, JDBCType.TINYINT);
            put(Short.TYPE, JDBCType.SMALLINT);
            put(Integer.TYPE, JDBCType.INTEGER);
            put(Long.TYPE, JDBCType.BIGINT);
            put(Float.TYPE, JDBCType.FLOAT);
            put(Double.TYPE, JDBCType.DOUBLE);
            put(Void.TYPE, JDBCType.NULL);

            put(Boolean.class, JDBCType.BIT);
            put(Character.class, JDBCType.CHAR);
            put(Byte.class, JDBCType.TINYINT);
            put(Short.class, JDBCType.SMALLINT);
            put(Integer.class, JDBCType.INTEGER);
            put(Long.class, JDBCType.BIGINT);
            put(Float.class, JDBCType.FLOAT);
            put(Double.class, JDBCType.DOUBLE);

            put(byte[].class, JDBCType.BLOB);
            put(BigDecimal.class, JDBCType.DECIMAL);
            put(Number.class, JDBCType.DECIMAL);
            put(String.class, JDBCType.VARCHAR);

            put(java.util.UUID.class, JDBCType.VARCHAR);
            put(java.util.Date.class, JDBCType.TIMESTAMP);
            put(java.time.LocalDate.class, JDBCType.DATE);
            put(java.time.LocalTime.class, JDBCType.TIME);
            put(java.sql.Date.class, JDBCType.DATE);
            put(java.sql.Time.class, JDBCType.TIME);
            put(java.sql.Timestamp.class, JDBCType.TIMESTAMP);
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

        List<Field> allFields = findAllFields(entityClass);
        for(Field field : allFields) {
            loadColumnInfo(field, tableInfo);
        }

        List<ColumnInfo> keys = 
            tableInfo.getColumns().stream()
                .filter(c -> c.isPrimaryKey())
                .collect(Collectors.toList());
        tableInfo.setPrimaryKey(new PrimaryKey(keys));

        return tableInfo;
    }

    protected List<Field> findAllFields(Class<?> entityClass) {
        List<Field> allFields = new ArrayList<>(20);
        Stack<Class<?>> superClasses = new Stack<>();

        Action1<Class<?>> fillAction = c -> {
            Field[] fields = c.getDeclaredFields();
            for(int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                allFields.add(field);
            }
        };

        Class<?> superClass = entityClass;
        while((superClass = superClass.getSuperclass()) != null) {
            if(superClass == Object.class) {
                break;
            }
            superClasses.push(superClass);
        }

        while(!superClasses.isEmpty()) {
            fillAction.apply(superClasses.pop());
        }

        fillAction.apply(entityClass);

        return allFields;
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
