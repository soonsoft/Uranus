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
import com.soonsoft.uranus.services.membership.po.AuthUserIdAndRoleId;

public class AuthUsersInRolesDAO extends BaseDAO {

    public int insert(AuthUserIdAndRoleId userIdAndRoleId) {
        return getMembershipAccess().insert("membership.auth_users_in_roles.insert", userIdAndRoleId);
    }

    public int deleteByUserId(UUID userId) {
        return getMembershipAccess().insert("membership.auth_users_in_roles.deleteByUserId", userId);
    }
    
    public List<AuthRole> selectByUserId(UUID userId) {
        Map<String, Object> params = MapUtils.createHashMap(1);
        params.put("userId", userId);
        return getMembershipAccess().select("membership.auth_users_in_roles.selectByUserId", params);
    }

    public Map<UUID, Set<Object>> selectByUsers(Collection<UUID> userIdList, Integer status) {
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

    public Map<UUID, Set<Object>> selectByRoles(Collection<UUID> roleIdList, Integer status) {
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

    private static UUID getUserId(AuthUserIdAndRoleId record) {
        return record.getUserId();
    }

    private static UUID getRoleId(AuthUserIdAndRoleId record) {
        return record.getRoleId();
    }

    private Map<UUID, Set<Object>> orderData(
        List<AuthUserIdAndRoleId> records, 
        Function<AuthUserIdAndRoleId, UUID> keyGetter,
        Function<AuthUserIdAndRoleId, Object> valueGetter) {
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