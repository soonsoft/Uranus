package com.soonsoft.uranus.api.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.api.interceptor.UserInfoInterceptor;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory;
import com.soonsoft.uranus.security.config.WebApplicationSecurityConfigFactory.WebApplicationSecurityConfigType;
import com.soonsoft.uranus.security.config.api.configurer.ApiSessionConfigurer;
import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import com.soonsoft.uranus.security.jwt.IRealHttpServletRequestHook;
import com.soonsoft.uranus.web.filter.HttpContextFilter;
import com.soonsoft.uranus.web.spring.WebApplicationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    @Autowired
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

        String sessionIdHeaderName = securityProperties.getSessionIdHeaderName();
        if(!StringUtils.isEmpty(sessionIdHeaderName)) {
            exposedHeaders.add(sessionIdHeaderName);
        }

        registry
            .addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .exposedHeaders(exposedHeaders.toArray(new String[exposedHeaders.size()]));
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
        IRealHttpServletRequestHook requestHook = new HeaderSessionIdHook();
        // Web-API应用程序，身份验证配置
        String sessionIdHeader = securityProperties.getSessionIdHeaderName();
        return new WebApplicationSecurityConfigFactory(
            WebApplicationSecurityConfigType.API, new ApiSessionConfigurer(sessionIdHeader, requestHook));
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