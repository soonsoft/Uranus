package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.po.SysMenu;


public class SysMenuDAO extends MybatisBaseDAO<SysMenu> implements IMapperID {

    public SysMenuDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public List<SysMenu> selectMenu(Map<String, Object> params) {
        return getDatabaseAccess().select(getStatement("selectMenu"), params);
    }
    
}