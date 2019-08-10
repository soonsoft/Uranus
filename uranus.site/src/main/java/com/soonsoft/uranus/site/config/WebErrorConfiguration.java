package com.soonsoft.uranus.site.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Resource;
import javax.servlet.Servlet;

import com.soonsoft.uranus.site.config.properties.ErrorPageProperties;

import java.util.List;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore({WebMvcAutoConfiguration.class})
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class WebErrorConfiguration {

    private final ServerProperties serverProperties;
    private final List<ErrorViewResolver> errorViewResolvers;

    @Resource
    private ErrorPageProperties errorPageProperties;

    public WebErrorConfiguration(ServerProperties serverProperties,
                                 ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
        this.serverProperties = serverProperties;
        this.errorViewResolvers = errorViewResolversProvider.getIfAvailable();
    }

    @Bean
    public ErrorPageRegistrar errorPageRegistrar(){
        return (registry -> {
            if(!StringUtils.isEmpty(errorPageProperties.getBadRequestPage())) {
                registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, errorPageProperties.getBadRequestPage()));
            }
            if(!StringUtils.isEmpty(errorPageProperties.getUnauthorizedPage())) {
                registry.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, errorPageProperties.getUnauthorizedPage()));
            }
            if(!StringUtils.isEmpty(errorPageProperties.getForbiddenPage())) {
                registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, errorPageProperties.getForbiddenPage()));
            }
            if(!StringUtils.isEmpty(errorPageProperties.getNotFoundPage())) {
                registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, errorPageProperties.getNotFoundPage()));
            }
            if(!StringUtils.isEmpty(errorPageProperties.getServerErrorPage())) {
                registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, errorPageProperties.getServerErrorPage()));
            }
        });
    }

    public List<ErrorViewResolver> getErrorViewResolvers() {
        return errorViewResolvers;
    }

    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public ErrorPageProperties getErrorPageProperties() {
        return errorPageProperties;
    }
}
