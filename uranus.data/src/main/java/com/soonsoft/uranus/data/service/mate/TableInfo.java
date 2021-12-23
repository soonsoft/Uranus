package com.soonsoft.uranus.data.service.mate;

import java.util.List;

public class TableInfo {

    private String tableName;

    private List<ColumnInfo> columns;

    private Class<?> entityType;

    private PrimaryKey primaryKey;

    private IDType primaryIdType = IDType.NONE;

    private String alias;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public IDType getPrimaryIdType() {
        return primaryIdType;
    }

    public void setPrimaryIdType(IDType primaryIdType) {
        this.primaryIdType = primaryIdType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
}
