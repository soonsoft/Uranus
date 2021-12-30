package com.soonsoft.uranus.services.membership.dao;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.SysFunction;

public class SysFunctionDAO extends MybatisBaseDAO<SysFunction> implements IMapperID {

    public SysFunctionDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }
}