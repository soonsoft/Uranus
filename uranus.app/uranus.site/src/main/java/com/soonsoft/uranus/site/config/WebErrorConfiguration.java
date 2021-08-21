package com.soonsoft.uranus.site.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.site.config.properties.ErrorPageProperties;

import java.util.List;

@Configuration
public class WebErrorConfiguration {

    private final List<ErrorViewResolver> errorViewResolvers;
    private final ErrorPageProperties errorPageProperties;

    @Autowired
    public WebErrorConfiguration(
        ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider,
        ErrorPageProperties errorPageProperties) {

        this.errorViewResolvers = errorViewResolversProvider.getIfAvailable();
        this.errorPageProperties = errorPageProperties;
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
}
