package com.soonsoft.uranus.api.config;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import com.soonsoft.uranus.api.config.properties.WebProperties;
import com.soonsoft.uranus.api.interceptor.UserInfoInterceptor;
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
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfiguration
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore({WebMvcAutoConfiguration.class})
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(new UserInfoInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(webProperties.getResourcePathList());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .exposedHeaders("Access-Control-Allow-Origin")
            .allowCredentials(true);
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
        return new WebApplicationContext();
    }
}