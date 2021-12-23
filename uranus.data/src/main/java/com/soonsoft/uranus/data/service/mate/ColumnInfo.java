package com.soonsoft.uranus.data.service.mate;

import java.sql.JDBCType;

public class ColumnInfo {

    private String columnName;

    private JDBCType columnType;

    private String entityFieldName;

    private Class<?> entityFieldType;

    private boolean primaryKey = false;

    private boolean notNull = false;

    private Object defaultValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public JDBCType getColumnType() {
        return columnType;
    }

    public void setColumnType(JDBCType columnType) {
        this.columnType = columnType;
    }

    public String getEntityFieldName() {
        return entityFieldName;
    }

    public void setEntityFieldName(String entityFieldName) {
        this.entityFieldName = entityFieldName;
    }

    public Class<?> getEntityFieldType() {
        return entityFieldType;
    }

    public void setEntityFieldType(Class<?> entityFieldType) {
        this.entityFieldType = entityFieldType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        if(primaryKey) {
            notNull = true;
        }
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    
}
