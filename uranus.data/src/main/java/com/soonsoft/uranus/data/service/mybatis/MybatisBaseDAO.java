package com.soonsoft.uranus.data.service.mybatis;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.mybatis.mapper.MappedStatementRegistry;

public abstract class MybatisBaseDAO<EntityType> {

    private final MybatisDatabaseAccess databaseAccess;
    private final Type entityType;

    public MybatisBaseDAO(IDatabaseAccess<?> databaseAccess) {
        this.databaseAccess = (MybatisDatabaseAccess) databaseAccess;

        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        entityType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public MybatisDatabaseAccess getDatabaseAccess() {
        return this.databaseAccess;
    }

    public int insert(EntityType data) {
        return this.databaseAccess.insert(getStatementName("insert"), data);
    }

    public int insertSelective(EntityType data) {
        return this.databaseAccess.insert(getStatementName("insertSelective"), data);
    }

    public int update(EntityType data) {
        return this.databaseAccess.update(getStatementName("update"), data);
    }

    public int updateSelective(EntityType data) {
        return this.databaseAccess.update(getStatementName("updateSelective"), data);
    }

    public int delete(Object... primaryValues) {
        return this.databaseAccess.delete(getStatementName("delete"), getPrimaryValue(primaryValues));
    }

    public EntityType getByPrimaryKey(Object... primaryValues) {
        return this.databaseAccess.get(getStatementName("getByPrimaryKey"), getPrimaryValue(primaryValues));
    }

    public String getStatementName(String name) {
        MappedStatementRegistry registry = this.databaseAccess.getMappedStatementRegistry();
        TableInfo tableInfo = registry.findTableInfo(entityType.getTypeName());
        if(tableInfo == null) {
            throw new IllegalStateException("the entityClass [" + entityType.getTypeName() + "] not register.");
        }
        return registry.getNamespace(tableInfo) + "." + name;
    }

    private Map<String, Object> getPrimaryValue(Object[] primaryValues) {
        if(primaryValues == null || primaryValues.length == 0) {
            throw new IllegalArgumentException("the parameter primaryValues is required.");
        }
        Map<String, Object> param = new HashMap<>();
        for(int i = 0; i < primaryValues.length; i++) {
            param.put("p" + i, primaryValues[i]);
        }

        return param;
    }
    
}