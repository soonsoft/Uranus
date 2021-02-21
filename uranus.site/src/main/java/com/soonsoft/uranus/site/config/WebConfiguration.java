package com.soonsoft.uranus.site.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.Servlet;
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
import com.soonsoft.uranus.site.interceptor.UserInfoInterceptor;
import com.soonsoft.uranus.web.filter.HttpContextFilter;
import com.soonsoft.uranus.web.spring.WebApplicationContext;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@AutoConfigureBefore({ WebMvcAutoConfiguration.class })
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    private SecurityProperties securityProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(securityProperties.getResourcePathList());
    }

    @Bean
    public WebApplicationSecurityConfigFactory webApplicationSecurityConfigFactory() {
        WebApplicationSecurityConfigFactory factory = new WebApplicationSecurityConfigFactory(WebApplicationSecurityConfigType.SITE);
        factory.setInitModuleAction((userManager, roleManager, functionManager, userProfile) -> {
            initUserManager(userManager);
            initRoleManager(roleManager);
            initFunctionManager(functionManager);
        });
        return factory;
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

    private void initUserManager(IUserManager userManager) {
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

        ((SimpleUserManager)userManager).addAll(users);
    }

    private void initRoleManager(IRoleManager roleManager) {
        List<RoleInfo> roles = new ArrayList<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        ((SimpleRoleManager)roleManager).setRoleInfos(roles);
    }

    private void initFunctionManager(IFunctionManager functionManager) {
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

        ((SimpleFunctionManager)functionManager).setFunctions(menus);
    }
}