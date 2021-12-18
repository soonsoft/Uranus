package com.soonsoft.uranus.services.membership.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.services.membership.po.SysMenu;

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
    
    public int delete(UUID functionId) {
        return getMembershipAccess().delete("membership.sys_menu.deleteMenu", functionId);
    }

    public List<SysMenu> select(Map<String, Object> params) {
        return getMembershipAccess().select("membership.sys_menu.select", params);
    }
    
}