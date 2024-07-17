package com.soonsoft.uranus.api.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.api.interceptor.UserInfoInterceptor;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory.WebApplicationSecurityConfigType;
import com.soonsoft.uranus.security.config.api.IRealHttpServletRequestHook;
import com.soonsoft.uranus.security.config.api.jwt.JWTConfigurer;
import com.soonsoft.uranus.security.config.api.session.ApiSessionConfigurer;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.simple.service.MemoryRefreshTokenStorage;
import com.soonsoft.uranus.security.simple.service.SimpleFunctionManager;
import com.soonsoft.uranus.security.simple.service.SimpleRoleManager;
import com.soonsoft.uranus.security.simple.service.SimpleUserManager;
import com.soonsoft.uranus.web.filter.HttpContextFilter;
import com.soonsoft.uranus.web.spring.WebApplicationContext;

import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    public WebConfiguration(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(new UserInfoInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(securityProperties.getResourcePathList());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        List<String> exposedHeaders = new ArrayList<>();
        exposedHeaders.add("Access-Control-Allow-Origin");

        String sessionIdHeaderName = securityProperties.getAccessTokenHeaderName();
        if(!StringUtils.isEmpty(sessionIdHeaderName)) {
            exposedHeaders.add(sessionIdHeaderName);
        }

        registry
            .addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
            .exposedHeaders(exposedHeaders.toArray(new String[exposedHeaders.size()]));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> newConverters = new ArrayList<>();
        newConverters.add(new GenericJsonHttpMessageConverter());
        newConverters.addAll(converters);

        converters.clear();
        converters.addAll(newConverters);
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
    public WebApplicationSecurityConfigFactory webApplicationSecurityConfigFactory() {
        return createJWTFactory();
    }

    protected WebApplicationSecurityConfigFactory createSesstionIdFactory() {
        IRealHttpServletRequestHook requestHook = new HeaderSessionIdHook();
        // Web-API应用程序（多页应用），身份验证配置
        String sessionIdHeader = securityProperties.getAccessTokenHeaderName();
        WebApplicationSecurityConfigFactory factory = new WebApplicationSecurityConfigFactory(
            WebApplicationSecurityConfigType.API, new ApiSessionConfigurer(sessionIdHeader, requestHook));
        factory.setInitModuleAction((userManager, roleManager, functionManager, userProfile) -> {
            initUserManager(userManager);
            initRoleManager(roleManager);
            initFunctionManager(functionManager);
        });
        return factory;
    }

    protected WebApplicationSecurityConfigFactory createJWTFactory() {
        // Web-API应用程序（单页应用），身份验证配置
        String accessTokenHeaderName = securityProperties.getAccessTokenHeaderName();
        WebApplicationSecurityConfigFactory factory = new WebApplicationSecurityConfigFactory(
            WebApplicationSecurityConfigType.API, new JWTConfigurer(accessTokenHeaderName, new MemoryRefreshTokenStorage(21600)));
        factory.setInitModuleAction((userManager, roleManager, functionManager, userProfile) -> {
            initUserManager(userManager);
            initRoleManager(roleManager);
            initFunctionManager(functionManager);
        });
        return factory;
    }

    //#region 测试数据

    private void initUserManager(IUserManager userManager) {
        Set<RoleInfo> roles = new HashSet<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        
        List<UserInfo> users = new ArrayList<>();
        String salt = null;
        String password = "1";
        UserInfo user = new UserInfo();
        user.setUserName("admin");
        user.setUserId(UUID.randomUUID().toString());
        user.setNickName("张三");
        user.setRoles(roles);
        user.setPassword(password, salt);
        user.setCellPhone("139-0099-8877");
        user.setCreateTime(new Date());
        users.add(user);

        ((SimpleUserManager) userManager).addAll(users);
    }

    private void initRoleManager(IRoleManager roleManager) {
        List<RoleInfo> roles = new ArrayList<>();
        roles.add(new RoleInfo("Admin", "管理员"));
        ((SimpleRoleManager) roleManager).setRoleInfos(roles);
    }

    private void initFunctionManager(IFunctionManager functionManager) {
        List<RoleInfo> allowRoles = new ArrayList<>();
        allowRoles.add(new RoleInfo("Admin"));

        List<FunctionInfo> functions = new ArrayList<>();
        FunctionInfo function = new FunctionInfo("1", "queryProductList", "/product/list");
        functions.add(function);

        function = new FunctionInfo("2", "addProduct", "product/add");
        functions.add(function);

        function = new FunctionInfo("3", "editProduct", "product/edit");
        functions.add(function);

        function = new FunctionInfo("4", "removeProduct", "product/remove");
        functions.add(function);

        function = new FunctionInfo("5", "checkProduct", "product/check");
        functions.add(function);

        ((SimpleFunctionManager) functionManager).setFunctions(functions);
    }

    //#endregion

    // 对Action返回String类型进行包装
    private static class GenericJsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

        @Override
        public boolean canWrite(
                @Nullable Type type, 
                Class<?> clazz, 
                @Nullable MediaType mediaType) {

            return type instanceof Class && CharSequence.class.isAssignableFrom((Class<?>) type) && CharSequence.class.isAssignableFrom(clazz);
        }

        @Override
        protected void addDefaultHeaders(
                HttpHeaders headers, Object t, 
                @Nullable MediaType contentType) throws IOException {
                    
            super.addDefaultHeaders(headers, t, MediaType.APPLICATION_JSON);
        }
    }

    // 从HttpRequestHeader中初始化Session，替换原来的cookie
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