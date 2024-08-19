package com.soonsoft.uranus.security.simple.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

public class SimpleFunctionManager implements IFunctionManager {

    private List<? extends FunctionInfo> functions;

    public SimpleFunctionManager() {

    }

    public SimpleFunctionManager(List<? extends FunctionInfo> functions) {
        setFunctions(functions);
    }

    public void setFunctions(List<? extends FunctionInfo> functions) {
        this.functions = functions;
    }

    @Override
    public List<? extends FunctionInfo> getEnabledFunctions() {
        if (functions == null) {
            return new ArrayList<>(0);
        }
        return functions;
    }

    @Override
    public List<MenuInfo> getMenus(UserInfo user) {
        if (user != null) {
            Set<RoleInfo> authorities = user.getRoles();
            if (authorities != null && !authorities.isEmpty()) {
                Set<String> userRoles = new HashSet<>();
                authorities.forEach(i -> userRoles.add(i.getRoleCode()));

                List<MenuInfo> menus = getEnabledMenus();
                List<MenuInfo> userMenus = new ArrayList<>(menus.size());
                for (MenuInfo menu : menus) {
                    List<String> roles = menu.getAllowRoles();
                    for (String roleCode : roles) {
                        if (userRoles.contains(roleCode)) {
                            userMenus.add(menu);
                            break;
                        }
                    }
                }
                return userMenus;
            }
        }
        return null;
    }

    @Override
    public List<? extends FunctionInfo> queryFunctions(String status, String... resourceTypes) {
        return this.functions;
    }

    @Override
    public List<MenuInfo> queryMenus(Map<String, Object> params) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryMenus'");
    }

    @Override
    public boolean createMenu(MenuInfo menu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMenu'");
    }

    @Override
    public boolean updateMenu(MenuInfo menu) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMenu'");
    }

    
}