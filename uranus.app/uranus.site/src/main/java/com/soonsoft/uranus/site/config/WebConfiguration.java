package com.soonsoft.uranus.site.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.Filter;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory.WebApplicationSecurityConfigType;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.simple.service.SimpleFunctionManager;
import com.soonsoft.uranus.security.simple.service.SimpleRoleManager;
import com.soonsoft.uranus.security.simple.service.SimpleUserManager;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.SysMenu;
import com.soonsoft.uranus.services.membership.service.FunctionService;
import com.soonsoft.uranus.services.membership.service.RoleService;
import com.soonsoft.uranus.site.interceptor.UserInfoInterceptor;
import com.soonsoft.uranus.site.service.LoginService;
import com.soonsoft.uranus.web.filter.HttpContextFilter;
import com.soonsoft.uranus.web.spring.WebApplicationContext;

import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    public WebConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(securityProperties.getResourcePathList());
    }

    @Bean
    public DelegatingFilterProxyRegistrationBean httpContextFilterRegistrationBean() {
        DelegatingFilterProxyRegistrationBean registrationBean = new DelegatingFilterProxyRegistrationBean("httpContextFilter");
        return registrationBean;
    }

    @Bean("httpContextFilter")
    public Filter httpContextFilter() {
        return new HttpContextFilter();
    }
    
    @Bean
    public ApplicationContextAware applicationContextAware() {
        return WebApplicationContext.getInstance();
    }

    @Bean
    public WebApplicationSecurityConfigFactory webApplicationSecurityConfigFactory(LoginService loginService) {
        WebApplicationSecurityConfigFactory factory = new WebApplicationSecurityConfigFactory(WebApplicationSecurityConfigType.SITE);
        factory.setInitModuleAction((userManager, roleManager, functionManager, userProfile) -> {
            if(userManager instanceof SimpleUserManager) {
                initUserManager(userManager);
            }
            
            if(roleManager instanceof SimpleRoleManager) {
                initRoleManager(roleManager);
            } else if(roleManager instanceof RoleService) {
                ((RoleService)roleManager).addRoleChanged(e -> {
                    AuthRole changedRole = e.getData();
                    List<Object> functions = changedRole.getMenus();
                    List<String> functionIdList = null;
                    if(!CollectionUtils.isEmpty(functions)) {
                        functionIdList = new ArrayList<>(functions.size());
                        for(Object item : functions) {
                            if(item instanceof String) {
                                functionIdList.add((String) item);
                            } else if(item instanceof SysMenu) {
                                functionIdList.add(((SysMenu) item).getFunctionId().toString());
                            }
                        }
                    }
                    ((FunctionService) functionManager).updateFunctionStore(changedRole.getRoleId().toString(), functionIdList);
                });
            }

            if(functionManager instanceof SimpleFunctionManager) {
                initFunctionManager(functionManager);
            } else if(functionManager instanceof FunctionService) {
                // Membership 需要初始化功能管理器
                // ((FunctionService)functionManager).initFunctionManager(functionManager);
            }
        });
        factory.setLoginPasswordFn((userName, password, userManager) -> loginService.loginByPassword(userName, password, userManager));

        return factory;
    }

    //#region 测试数据

    private void initUserManager(IUserManager userManager) {
        Set<RoleInfo> roles = new HashSet<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        
        List<UserInfo> users = new ArrayList<>();
        String salt = null;
        String password = userManager.encryptPassword("1", salt);
        UserInfo user = new UserInfo();
        user.setUserName("admin");
        user.setUserId(UUID.randomUUID().toString());
        user.setNickName("张三");
        user.setRoles(roles);
        user.setPassword(password, salt);
        user.setCellPhone("139-0099-8877");
        user.setCreateTime(new Date());
        users.add(user);

        ((SimpleUserManager)userManager).addAll(users);
    }

    private void initRoleManager(IRoleManager roleManager) {
        List<RoleInfo> roles = new ArrayList<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        ((SimpleRoleManager)roleManager).setRoleInfos(roles);
    }

    private void initFunctionManager(IFunctionManager functionManager) {
        List<String> allowRoles = new ArrayList<>();
        allowRoles.add("Admin");

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

        menu = new MenuInfo("4_1", "用户管理", "/settings/users");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_2", "角色管理", "/settings/roles");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_3", "模块管理", "/settings/menus");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        menu = new MenuInfo("4_4", "系统设置", "/coming-soon");
        menu.setParentResourceCode("4");
        menu.setAllowRoles(allowRoles);
        menus.add(menu);

        ((SimpleFunctionManager)functionManager).setFunctions(menus);
    }

    //#endregion
}