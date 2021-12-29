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
import com.soonsoft.uranus.services.membership.po.AuthUserRoleRelation;

public class AuthUserRoleRelationDAO extends MybatisBaseDAO<AuthUserRoleRelation> {

    public AuthUserRoleRelationDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public int deleteByUserId(UUID userId) {
        return getDatabaseAccess().insert("uranus.membership.deleteUserRoleByUserId", userId);
    }
    
    public List<AuthRole> selectByUserId(UUID userId) {
        Map<String, Object> params = MapUtils.createHashMap(1);
        params.put("userId", userId);
        return getDatabaseAccess().select("uranus.membership.selectRolesByUserId", params);
    }

    public Map<UUID, Set<Object>> selectByUsers(Collection<UUID> userIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("users", userIdList);
        if(status != null) {
            params.put("status", status);
        }

        List<AuthUserRoleRelation> records =  getDatabaseAccess().select("uranus.membership.selectUserRoleByUsers", params);
        return orderData(
            records, 
            AuthUserRoleRelationDAO::getUserId, 
            roleIdAndName -> roleIdAndName
        );
    }

    public Map<UUID, Set<Object>> selectByRoles(Collection<UUID> roleIdList, Integer status) {
        Map<String, Object> params = MapUtils.createHashMap(2);
        params.put("roles", roleIdList);
        if(status != null) {
            params.put("status", status);
        }

        List<AuthUserRoleRelation> records =  getDatabaseAccess().select("uranus.membership.selectUserRoleByRoles", params);
        return orderData(
            records, 
            AuthUserRoleRelationDAO::getRoleId, 
            userIdAndName -> userIdAndName
        );
    }

    private static UUID getUserId(AuthUserRoleRelation record) {
        return record.getUserId();
    }

    private static UUID getRoleId(AuthUserRoleRelation record) {
        return record.getRoleId();
    }

    private Map<UUID, Set<Object>> orderData(
        List<AuthUserRoleRelation> records, 
        Function<AuthUserRoleRelation, UUID> keyGetter,
        Function<AuthUserRoleRelation, Object> valueGetter) {
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