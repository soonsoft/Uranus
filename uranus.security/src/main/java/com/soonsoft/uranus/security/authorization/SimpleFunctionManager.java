package com.soonsoft.uranus.security.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.springframework.security.core.GrantedAuthority;;

/**
 * SimpleFunctionManager
 */
public class SimpleFunctionManager implements IFunctionManager {

    private List<MenuInfo> menus;

    public SimpleFunctionManager(List<MenuInfo> menus) {
        this.menus = menus;
    }

    @Override
    public List<MenuInfo> getEnabledMenus() {
        if(menus == null) {
            return new ArrayList<>(0);
        }

        return menus;
    }

    @Override
    public List<MenuInfo> getMenus(UserInfo user) {
        if(user != null) {
            Collection<GrantedAuthority> authorities =  user.getAuthorities();
            if(authorities != null && !authorities.isEmpty()) {
                Set<String> userRoles = new HashSet<>();
                authorities.forEach(i -> userRoles.add(i.getAuthority()));
                
                List<MenuInfo> userMenus = new ArrayList<>(menus.size());
                for(MenuInfo menu : menus) {
                    List<RoleInfo> roles = menu.getAllowRoles();
                    for(RoleInfo role : roles) {
                        if(userRoles.contains(role.getAuthority())) {
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

    
}