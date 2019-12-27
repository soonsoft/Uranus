package com.soonsoft.uranus.services.membership.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthRoleIdAndFunctionId;
import com.soonsoft.uranus.core.common.collection.MapUtils;

/**
 * AuthRolesInFunctionsDAO
 */
public class AuthRolesInFunctionsDAO extends BaseDAO {

    public int insert(AuthRoleIdAndFunctionId record) {
        return getMembershipAccess().insert("membership.auth_roles_in_functions.insert", record);
    }

    public int deleteByRoleId(String roleId) {
        return getMembershipAccess().delete("membership.auth_roles_in_functions.deleteByRoleId", roleId);
    }

    public int deleteByFunctionId(String functionId) {
        return getMembershipAccess().delete("membership.auth_roles_in_functions.deleteByFunctionId", functionId);
    }

    public Map<String, Set<Object>> selectByFunctions(List<String> functionIdList, Integer status) {
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

    public Map<String, Set<Object>> selectByRoles(List<String> roleIdList, Integer status) {
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

    private static String getFunctionId(AuthRoleIdAndFunctionId record) {
        return record.getFunctionId();
    }

    private static String getRoleId(AuthRoleIdAndFunctionId record) {
        return record.getRoleId();
    }

    private Map<String, Set<Object>> orderData(
        List<AuthRoleIdAndFunctionId> records, 
        Function<AuthRoleIdAndFunctionId, String> keyGetter,
        Function<AuthRoleIdAndFunctionId, Object> valueGetter) {
        if(records == null || records.isEmpty()) {
            return null;
        }
        Map<String, Set<Object>> value = MapUtils.createHashMap(32);
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