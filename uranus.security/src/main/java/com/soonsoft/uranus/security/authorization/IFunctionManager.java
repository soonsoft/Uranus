package com.soonsoft.uranus.security.authorization;

import java.util.List;

import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

/**
 * IFunctionManager
 */
public interface IFunctionManager {

    /**
     * 获取有效的菜单列表
     * @return
     */
    List<MenuInfo> getEnabledMenus();

    /**
     * 获取用户对应的菜单列表
     * @param user 用户信息
     * @return
     */
    List<MenuInfo> getMenus(UserInfo user);

}