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

    /**
     * Insert 实体数据
     * @param data 实体数据
     * @return 影响行数
     */
    public int insert(EntityType data) {
        return this.databaseAccess.insert(getStatementName("insert"), data);
    }

    /**
     * Insert 实体数据，忽略 null 字段
     * @param data 实体数据
     * @return 影响行数
     */
    public int insertSelective(EntityType data) {
        return this.databaseAccess.insert(getStatementName("insertSelective"), data);
    }

    /**
     * Update 实体数据（全部字段，将主键作为where条件，所以主键字段不能为 null）
     * @param data 实体数据
     * @return 影响行数
     */
    public int update(EntityType data) {
        return this.databaseAccess.update(getStatementName("update"), data);
    }

    /**
     * Update 实体数据，忽略 null 字段
     * @param data 实体数据
     * @return 影响行数
     */
    public int updateSelective(EntityType data) {
        return this.databaseAccess.update(getStatementName("updateSelective"), data);
    }

    /**
     * Delete 实体数据，通过主键字段作为 where 条件
     * @param primaryValues 主键值
     * @return 影响行数
     */
    public int delete(Object... primaryValues) {
        return this.databaseAccess.delete(getStatementName("delete"), getPrimaryValue(primaryValues));
    }

    /**
     * 根据主键获取实体数据
     * @param primaryValues 主键值
     * @return 实体数据
     */
    public EntityType getByPrimary(Object... primaryValues) {
        return this.databaseAccess.get(getStatementName("getByPrimary"), getPrimaryValue(primaryValues));
    }

    private String getStatementName(String name) {
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