package com.soonsoft.uranus.services.membership.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthUserIdAndRoleId;

/**
 * AuthUsersInRolesDAO
 */
public class AuthUsersInRolesDAO extends BaseDAO {

    public int insert(AuthUserIdAndRoleId userIdAndRoleId) {
        return getMembershipAccess().insert("membership.auth_users_in_roles.insert", userIdAndRoleId);
    }

    public int deleteByUserId(String userId) {
        return getMembershipAccess().insert("membership.auth_users_in_roles.deleteByUserId", userId);
    }
    
    public List<AuthRole> selectByUserId(String userId) {
        Map<String, Object> params = MapUtils.createHashMap(1);
        params.put("userId", userId);
        return getMembershipAccess().select("membership.auth_users_in_roles.selectByUserId", params);
    }

    public Map<String, Set<Object>> selectByUsers(List<String> userIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("users", userIdList);
        if(status != null) {
            params.put("status", status);
        }

        List<AuthUserIdAndRoleId> records =  getMembershipAccess().select("membership.auth_users_in_roles.selectByUsers", params);
        return orderData(
            records, 
            AuthUsersInRolesDAO::getUserId, 
            roleIdAndName -> roleIdAndName
        );
    }

    public Map<String, Set<Object>> selectByRoles(List<String> roleIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("roles", roleIdList);
        if(status != null) {
            params.put("status", status);
        }

        List<AuthUserIdAndRoleId> records =  getMembershipAccess().select("membership.auth_users_in_roles.selectByRoles", params);
        return orderData(
            records, 
            AuthUsersInRolesDAO::getRoleId, 
            userIdAndName -> userIdAndName
        );
    }

    private static String getUserId(AuthUserIdAndRoleId record) {
        return record.getUserId();
    }

    private static String getRoleId(AuthUserIdAndRoleId record) {
        return record.getRoleId();
    }

    private Map<String, Set<Object>> orderData(
        List<AuthUserIdAndRoleId> records, 
        Function<AuthUserIdAndRoleId, String> keyGetter,
        Function<AuthUserIdAndRoleId, Object> valueGetter) {
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