package com.soonsoft.uranus.services.membership.dao;


import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.AuthPassword;

public class AuthPasswordDAO extends MybatisBaseDAO<AuthPassword> {

    public AuthPasswordDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

}