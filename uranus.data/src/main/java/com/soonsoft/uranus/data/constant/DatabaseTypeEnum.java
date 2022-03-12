package com.soonsoft.uranus.data.constant;

public enum DatabaseTypeEnum {

    MySQL("MySQL", "com.mysql.cj.jdbc.Driver", "8"),
    MySQL5("MySQL", "com.mysql.jdbc.Driver", "5"),
    PostgreSQL("PostgreSQL", "org.postgresql.Driver", null),
    SQLServer("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", null),
    Oracle("Oracle", "oracle.jdbc.OracleDriver", null),
    ;

    private String databaseName;
    private String driverClassName;
    private String version;

    private DatabaseTypeEnum(String name, String driverClassName, String version) {
        this.databaseName = name;
        this.driverClassName = driverClassName;
        this.version = version;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getVersion() {
        return this.version;
    }

    public static String findDatabaseName(String driverClassName) {
        for(DatabaseTypeEnum type : values()) {
            if(type.getDriverClassName().equals(driverClassName)) {
                return type.getDatabaseName();
            }
        }
        return null;
    }
    
}
