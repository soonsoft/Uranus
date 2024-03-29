package com.soonsoft.uranus.services.membership.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthPermission;
import com.soonsoft.uranus.services.membership.po.FunctionRole;

public class AuthPermissionDAO extends MybatisBaseDAO<AuthPermission> implements IMapperID {

    public AuthPermissionDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public int deleteByRoleId(UUID roleId) {
        return getDatabaseAccess().delete(getStatement("deletePermissionByRoleId"), roleId);
    }

    public int deleteByFunctionId(UUID functionId) {
        return getDatabaseAccess().delete(getStatement("deletePermissionByFunctionId"), functionId);
    }

    public Map<UUID, Set<Object>> selectByFunctions(Collection<UUID> functionIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("functions", functionIdList);
        if(status != null) {
            params.put("status", status);
        }
        List<FunctionRole> records = getDatabaseAccess().select(getStatement("selectPermissionByFunctions"), params);
        return orderData(
            records, 
            e -> ((FunctionRole) e).getFunctionId(), 
            e -> {
                FunctionRole i = (FunctionRole) e;
                AuthRole role = new AuthRole();
                role.setRoleId(i.getRoleId());
                role.setRoleName(i.getRoleName());
                role.setDescription(i.getDescription());
                role.setStatus(i.getStatus());
                return role;
            }
        );
    }

    public Map<UUID, Set<Object>> selectByRoles(Collection<UUID> roleIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("roles", roleIdList);
        if(status != null) {
            params.put("status", status);
        }
        List<AuthPermission> records =  getDatabaseAccess().select(getStatement("selectPermissionByRoles"), params);
        return orderData(
            records, 
            e -> ((AuthPermission) e).getRoleId(),
            e -> ((AuthPermission) e).getFunctionId()
        );
    }

    private Map<UUID, Set<Object>> orderData(
        List<?> records, 
        Function<Object, UUID> keyGetter,
        Function<Object, Object> valueGetter) {
        if(records == null || records.isEmpty()) {
            return null;
        }
        Map<UUID, Set<Object>> value = MapUtils.createHashMap(32);
        records.forEach(i -> {
            Set<Object> collection = value.get(keyGetter.apply(i));
            if(collection == null) {
                collection = new HashSet<>();
                value.put(keyGetter.apply(i), collection);
            }
            collection.add(valueGetter.apply(i));
        });
        return value;
    }
    
}