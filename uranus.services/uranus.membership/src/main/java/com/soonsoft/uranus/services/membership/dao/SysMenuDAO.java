package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.services.membership.dto.SysMenu;

/**
 * SysMenuDAO
 */
public class SysMenuDAO extends BaseDAO {

    public int insert(SysMenu menu) {
        return getMembershipAccess().insert("membership.sys_menu.insertMenu", menu);
    }

    public int update(SysMenu menu) {
        return getMembershipAccess().update("membership.sys_menu.updateMenu", menu);
    }
    
    public int delete(String functionId) {
        return getMembershipAccess().delete("membership.sys_menu.deleteMenu", functionId);
    }

    public List<SysMenu> select(Map<String, Object> params) {
        return getMembershipAccess().select("membership.sys_menu.select", params);
    }
    
}