package com.soonsoft.uranus.security.config;

import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authentication.WebUserDetailsService;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.WebAuthorizationManager;
import com.soonsoft.uranus.security.authorization.WebSecurityMetadataSource;
import com.soonsoft.uranus.security.authorization.voter.PermissionVoter;
import com.soonsoft.uranus.security.authorization.voter.PrivilegeVoter;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseSecurityConfiguration {
    
    public abstract SecurityProperties getSecurityProperties();

    public abstract ApplicationContext getApplicationContext();

    /**
     * 自定义身份验证管理器
     */
    @Bean
    public WebUserDetailsService webUserDetailsService(@Qualifier("userManager") IUserManager userManager) {
        WebUserDetailsService userDetailsService = new WebUserDetailsService();
        userDetailsService.setUserManager(userManager);
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        SecurityProperties securityProperties = getSecurityProperties();
        if(securityProperties == null) {
            return web -> {};
        }
        return web -> {
            // 配置静态资源，这些资源不做安全验证
            web.ignoring()
                .requestMatchers(
                    HttpMethod.GET, 
                    securityProperties.getResourcePathArray());
        };
    }

    protected void configure(HttpSecurity http) {
        ApplicationContext context = this.getApplicationContext();

        // 初始化SecurityManager
        SecurityManager.init(context);

        // 获取配置器，配置WebSite或是WebAPI
        WebApplicationSecurityConfigFactory factory = context.getBean(WebApplicationSecurityConfigFactory.class);
        factory.applyInitModuleAction();

        // Web应用程序，身份验证配置
        WebApplicationSecurityConfig config = factory.create();

        IFunctionManager functionManager = SecurityManager.current().getFunctionManager();
        final WebSecurityMetadataSource securityMetadataSource = new WebSecurityMetadataSource();
        // TODO 当前，系统菜单和角色对应关系是在系统启动时就加载好了，变更需要重启系统。后续，更新角色和菜单绑定关系后，动态刷新菜单权限资源
        securityMetadataSource.setConfigAttributeCollection(functionManager.getEnabledMenus());

        WebAuthorizationManager authorizationManager = 
            new WebAuthorizationManager(request -> securityMetadataSource.getAttributes(request));
        authorizationManager.addVoter(new PrivilegeVoter());
        authorizationManager.addVoter(new PermissionVoter());

        config.setWebAuthorizationManager(authorizationManager);
        config.setWebSecurityMetadataSource(securityMetadataSource);
        config.config(http);
    }
    
}
