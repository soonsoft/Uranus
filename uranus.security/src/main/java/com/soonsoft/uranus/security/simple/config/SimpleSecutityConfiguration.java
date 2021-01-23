package com.soonsoft.uranus.security.simple.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.BaseSecurityConfiguration;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.simple.service.SimpleFunctionManager;
import com.soonsoft.uranus.security.simple.service.SimpleRoleManager;
import com.soonsoft.uranus.security.simple.service.SimpleUserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SimpleSecutityConfiguration extends BaseSecurityConfiguration {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Bean(name = "userManager")
    public IUserManager userManager(PasswordEncoder passwordEncoder) {
            
        SimpleUserManager userManager = new SimpleUserManager(passwordEncoder);

        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        
        List<UserInfo> users = new ArrayList<>();
        String salt = null;
        UserInfo user = new UserInfo("admin", userManager.encryptPassword("1", salt), roles);
        user.setNickName("张三");
        user.setPasswordSalt(salt);
        user.setCellPhone("139-0099-8877");
        user.setCreateTime(new Date());
        users.add(user);

        userManager.addAll(users); 

        return userManager;
    }

    @Bean(name = "roleManager")
    public IRoleManager roleManager() {
        return new SimpleRoleManager();
    }

    @Bean(name = "functionManager")
    public IFunctionManager functionManager() {
        List<RoleInfo> allowRoles = new ArrayList<>();
        allowRoles.add(new RoleInfo("Admin"));

        List<MenuInfo> menus = new ArrayList<>();
        MenuInfo menu = new MenuInfo("1", "HOME", "/index");
        menu.setIcon("/content/icons/sys-setting.png");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("2", "功能菜单2", "/coming-soon");
        menu.setIcon("/content/icons/sys-setting.png");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("3", "功能菜单3", null);
        menu.setIcon("/content/icons/sys-setting.png");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("3_1", "菜单3-功能", "/coming-soon");
        menu.setParentResourceCode("3");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("3_2", "菜单3-功能", "/coming-soon");
        menu.setParentResourceCode("3");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("3_3", "菜单3-功能", "/coming-soon");
        menu.setParentResourceCode("3");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4", "系统管理", null);
        menu.setIcon("/content/icons/sys-setting.png");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_1", "用户管理", "/coming-soon");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_2", "角色管理", "/coming-soon");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_3", "模块管理", "/coming-soon");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_4", "系统设置", "/coming-soon");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        return new SimpleFunctionManager(menus);
    }
    
}
