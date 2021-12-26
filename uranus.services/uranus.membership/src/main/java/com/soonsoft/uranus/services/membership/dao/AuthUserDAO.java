package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthUser;

public class AuthUserDAO extends MybatisBaseDAO<AuthUser> {

    public AuthUserDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public AuthUser getUser(String userName) {
        return getDatabaseAccess().get("uranus.membership.getUserByUserName", userName);
    }

    public int deleteUser(String username) {
        return getDatabaseAccess().delete("uranus.membership.deleteUserByUserName", username);
    }

    public List<AuthUser> selectUser(Map<String, Object> params, Page page) {
        return getDatabaseAccess().select("uranus.membership.selectUser", params, page);
    }
    
}