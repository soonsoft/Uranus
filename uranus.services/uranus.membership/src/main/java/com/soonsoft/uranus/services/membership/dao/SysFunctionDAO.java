package com.soonsoft.uranus.services.membership.dao;

import com.soonsoft.uranus.services.membership.po.SysMenu;

/**
 * SysFunctionDAO
 */
public class SysFunctionDAO extends BaseDAO {

    public int insert(SysMenu menu) {
        return getMembershipAccess().insert("membership.sys_menu.insertFunction", menu);
    }

    public int update(SysMenu menu) {
        return getMembershipAccess().update("membership.sys_menu.updateFunction", menu);
    }
    
    public int delete(String functionId) {
        return getMembershipAccess().delete("membership.sys_menu.deleteFunction", functionId);
    }
}