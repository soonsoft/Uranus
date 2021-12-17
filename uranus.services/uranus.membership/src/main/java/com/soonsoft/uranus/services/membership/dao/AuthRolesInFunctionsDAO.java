package com.soonsoft.uranus.services.membership.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthRoleIdAndFunctionId;

/**
 * AuthRolesInFunctionsDAO
 */
public class AuthRolesInFunctionsDAO extends BaseDAO {

    public int insert(AuthRoleIdAndFunctionId record) {
        return getMembershipAccess().insert("membership.auth_roles_in_functions.insert", record);
    }

    public int deleteByRoleId(UUID roleId) {
        return getMembershipAccess().delete("membership.auth_roles_in_functions.deleteByRoleId", roleId);
    }

    public int deleteByFunctionId(UUID functionId) {
        return getMembershipAccess().delete("membership.auth_roles_in_functions.deleteByFunctionId", functionId);
    }

    public Map<UUID, Set<Object>> selectByFunctions(Collection<UUID> functionIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("functions", functionIdList);
        if(status != null) {
            params.put("status", status);
        }
        List<AuthRoleIdAndFunctionId> records = getMembershipAccess().select("membership.auth_roles_in_functions.selectByFunctions", params);
        return orderData(
            records, 
            AuthRolesInFunctionsDAO::getFunctionId, 
            i -> {
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
        List<AuthRoleIdAndFunctionId> records =  getMembershipAccess().select("membership.auth_roles_in_functions.selectByRoles", params);
        return orderData(
            records, 
            AuthRolesInFunctionsDAO::getRoleId, 
            AuthRolesInFunctionsDAO::getFunctionId
        );
    }

    private static UUID getFunctionId(AuthRoleIdAndFunctionId record) {
        return record.getFunctionId();
    }

    private static UUID getRoleId(AuthRoleIdAndFunctionId record) {
        return record.getRoleId();
    }

    private Map<UUID, Set<Object>> orderData(
        List<AuthRoleIdAndFunctionId> records, 
        Function<AuthRoleIdAndFunctionId, UUID> keyGetter,
        Function<AuthRoleIdAndFunctionId, Object> valueGetter) {
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