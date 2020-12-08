package com.soonsoft.uranus.security.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

/**
 * IFunctionManager
 */
public interface IFunctionManager {

    List<? extends FunctionInfo> getEnabledFunctions();

    /**
     * 获取有效的菜单列表
     * @return
     */
    default List<MenuInfo> getEnabledMenus() {
        List<? extends FunctionInfo> enabledFunctions = getEnabledFunctions();
        if(enabledFunctions == null) {
            return new ArrayList<>(0);
        }

        return enabledFunctions
                    .stream()
                    .filter(f -> f.isType(FunctionInfo.MENU_TYPE))
                    .map(f -> (MenuInfo)f)
                    .collect(Collectors.toList());
    }

    /**
     * 获取用户对应的菜单列表
     * @param user 用户信息
     * @return
     */
    List<MenuInfo> getMenus(UserInfo user);

}