package com.soonsoft.uranus.services.membership.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthPrivilege;

public class AuthPrivilegeDAO extends MybatisBaseDAO<AuthPrivilege> implements IMapperID {

    public AuthPrivilegeDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public int deletePrivilegeByUserId(UUID userId) {
        return getDatabaseAccess().delete(getStatement("deletePrivilegeByUserId"), userId);
    }

    public List<AuthPrivilege> selectUserPrivileges(UUID userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return getDatabaseAccess().select(getStatement("selectUserPrivileges"), param);
    }

    public List<AuthPrivilege> selectMutilUserPrivileges(List<UUID> userIdList) {
        Map<String, Object> param = new HashMap<>();
        param.put("userIdList", userIdList);
        return getDatabaseAccess().select(getStatement("selectMutilUserPrivileges"), param);
    }
    
}
