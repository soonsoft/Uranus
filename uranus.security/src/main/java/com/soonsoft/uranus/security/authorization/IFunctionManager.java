package com.soonsoft.uranus.security.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceStatus;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceType;


public interface IFunctionManager {

    /**
     * 查询功能列表
     * @param <T>
     * @param status 状态
     * @param resourceTypes 类型
     * @return
     */
    List<? extends FunctionInfo> queryFunctions(String status, String... resourceTypes);

    /**
     * 获取有效的功能列表
     * @return
     */
    default List<? extends FunctionInfo> getEnabledFunctions() {
        return queryFunctions(ResourceStatus.ENABLED);
    }

    /**
     * 获取有效的菜单列表
     * @return
     */
    default List<MenuInfo> getEnabledMenus() {
        List<? extends FunctionInfo> enabledFunctions = queryFunctions(ResourceStatus.ENABLED, ResourceType.MENU);
        if(enabledFunctions == null) {
            return new ArrayList<>(0);
        }

        return enabledFunctions
                    .stream()
                    .filter(f -> f.isType(ResourceType.MENU))
                    .map(f -> (MenuInfo) f)
                    .collect(Collectors.toList());
    }

    /**
     * 获取用户对应的菜单列表
     * @param user 用户信息
     * @return
     */
    List<MenuInfo> getMenus(UserInfo user);

    /**
     * 查询菜单列表
     * @param params 查询条件
     * @return
     */
    List<MenuInfo> queryMenus(Map<String, Object> params);

    /**
     * 创建菜单
     * @param menu 菜单信息
     * @return
     */
    boolean createMenu(MenuInfo menu);

    /**
     * 更新菜单
     * @param menu 菜单信息
     * @return
     */
    boolean updateMenu(MenuInfo menu);

}