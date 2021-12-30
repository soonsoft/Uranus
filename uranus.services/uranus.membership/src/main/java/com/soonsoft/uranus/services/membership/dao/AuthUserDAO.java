package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthUser;

public class AuthUserDAO extends MybatisBaseDAO<AuthUser> implements IMapperID {

    public AuthUserDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public AuthUser getUser(String userName) {
        return getDatabaseAccess().get(getStatement("getUserByUserName"), userName);
    }

    public int deleteUser(String username) {
        return getDatabaseAccess().delete(getStatement("deleteUserByUserName"), username);
    }

    public List<AuthUser> selectUser(Map<String, Object> params, Page page) {
        return getDatabaseAccess().select(getStatement("selectUser"), params, page);
    }
    
}