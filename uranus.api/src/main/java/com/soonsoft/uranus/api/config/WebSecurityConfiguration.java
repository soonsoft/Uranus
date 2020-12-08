package com.soonsoft.uranus.api.config;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authentication.WebUserDetailsService;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.WebApplicationConfig;
import com.soonsoft.uranus.security.config.api.configurer.ApiSessionConfigurer;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.jwt.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.authentication.SimpleUserManager;
import com.soonsoft.uranus.security.authorization.SimpleRoleManager;
import com.soonsoft.uranus.security.authorization.SimpleFunctionManager;

import com.soonsoft.uranus.services.membership.authorization.MembershipRoleVoter;
import com.soonsoft.uranus.api.config.properties.MembershipProperties;
import com.soonsoft.uranus.api.config.properties.WebProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * WebSecurityConfiguration
 */
@Configuration
@EnableConfigurationProperties(MembershipProperties.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private WebProperties webProperties;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 配置静态资源，这些资源不做安全验证
        web.ignoring()
            .antMatchers(HttpMethod.GET, webProperties.getResourcePathArray());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 初始化SecurityManager
        SecurityManager.init(this.getApplicationContext());
        IRealHttpServletRequestHook requestHook = new HeaderSessionIdHook();
        // Web-API应用程序，身份验证配置
        String sessionIdHeader = webProperties.getSessionIdHeaderName();
        WebApplicationConfig config = 
            SecurityManager.webApiApplicationConfig(http, new ApiSessionConfigurer(sessionIdHeader, requestHook));
        config.getWebAccessDecisionManager().addVoter(new MembershipRoleVoter());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(name = "userManager")
    public IUserManager userManager(@Qualifier("securityAccess") IDatabaseAccess securityAccess,
            PasswordEncoder passwordEncoder) {

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
    public IRoleManager roleManager(@Qualifier("securityAccess") IDatabaseAccess securityAccess) {
        return new SimpleRoleManager();
    }

    @Bean(name = "functionManager")
    public IFunctionManager functionManager(@Qualifier("securityAccess") IDatabaseAccess securityAccess) {
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

    /**
     * 自定义身份验证管理器
     */
    @Bean
    public WebUserDetailsService userDetailsService(@Qualifier("userManager") IUserManager userManager) {
        WebUserDetailsService userDetailsService = new WebUserDetailsService();
        userDetailsService.setUserManager(userManager);
        return userDetailsService;
    }

    private static class HeaderSessionIdHook implements IRealHttpServletRequestHook {

        private Field requestField;

        public HeaderSessionIdHook() {
            Class<org.apache.catalina.connector.RequestFacade> cls = org.apache.catalina.connector.RequestFacade.class;

            try {
                Field field = cls.getDeclaredField("request");
                field.setAccessible(true);
                this.requestField = field;
            } catch (Exception e) {
                this.requestField = null;
            }
        }

        @Override
        public HttpServletRequest getRealHttpServletRequest(ServletRequest request) {
            if (!(request instanceof HttpServletRequest)) {
                return null;
            }

            ServletRequest realRequest = request;

            if (realRequest instanceof org.apache.catalina.connector.Request) {
                return (HttpServletRequest) realRequest;
            }

            if (realRequest instanceof javax.servlet.ServletRequestWrapper) {
                realRequest = ((javax.servlet.ServletRequestWrapper) realRequest).getRequest();
                return getRealHttpServletRequest(realRequest);
            }

            if (realRequest instanceof org.apache.catalina.connector.RequestFacade) {
                realRequest = getRequest(((org.apache.catalina.connector.RequestFacade) realRequest));
                if(realRequest != null) {
                    return getRealHttpServletRequest(realRequest);
                }
            }

            return null;
        }

        @Override
        public void setSessionId(HttpServletRequest request, String sessionId) {
            org.apache.catalina.connector.Request realRequest = (org.apache.catalina.connector.Request) request;
            realRequest.setRequestedSessionId(sessionId);
            realRequest.setRequestedSessionCookie(false);
            realRequest.setRequestedSessionURL(false);
        }

        private ServletRequest getRequest(org.apache.catalina.connector.RequestFacade requestFacade) {
            if(requestField == null) {
                return null;
            }
            try {
                return (ServletRequest) requestField.get(requestFacade);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                return null;
            }
        }
        
    }

}