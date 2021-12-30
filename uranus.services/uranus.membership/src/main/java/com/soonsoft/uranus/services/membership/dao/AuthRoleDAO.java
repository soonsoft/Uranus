package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthRole;

public class AuthRoleDAO extends MybatisBaseDAO<AuthRole> implements IMapperID {

    public AuthRoleDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public int deleteRoleByRoleName(String roleName) {
        return getDatabaseAccess().delete(getStatement("deleteRoleByRoleName"), roleName);
    }

    public List<AuthRole> selectRole(Map<String, Object> params, Page page) {
        return getDatabaseAccess().select(getStatement("selectRole"), params, page);
    }

}