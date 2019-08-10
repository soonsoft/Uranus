package com.soonsoft.uranus.site.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:error-page.properties")
public class ErrorPageProperties {

    @Value("${error-page-400:}")
    private String badRequestPage;

    @Value("${error-message-400:您的Http Request数据可能不完整或已损坏}")
    private String badRequestMessage;

    @Value("${error-page-401:}")
    private String unauthorizedPage;

    @Value("${error-message-401:抱歉，您还没有登录}")
    private String unauthorizedMessage;

    @Value("${error-page-403:}")
    private String forbiddenPage;

    @Value("${error-message-403:抱歉，您没有权限访问}")
    private String forbiddenMessage;

    @Value("${error-page-404:}")
    private String notFoundPage;

    @Value("${error-message-404:抱歉，没有找到您请求的资源}")
    private String notFoundMessage;

    @Value("${error-page-500:}")
    private String serverErrorPage;

    @Value("${error-message-500:抱歉，服务器发生意外的错误，请稍后重试}")
    private String serverErrorMessage;

    public String getBadRequestPage() {
        return badRequestPage;
    }

    public String getBadRequestMessage() {
        return badRequestMessage;
    }

    public String getUnauthorizedPage() {
        return unauthorizedPage;
    }

    public String getUnauthorizedMessage() {
        return unauthorizedMessage;
    }

    public String getForbiddenPage() {
        return forbiddenPage;
    }

    public String getForbiddenMessage() {
        return forbiddenMessage;
    }

    public String getNotFoundPage() {
        return notFoundPage;
    }

    public String getNotFoundMessage() {
        return notFoundMessage;
    }

    public String getServerErrorPage() {
        return serverErrorPage;
    }

    public String getServerErrorMessage() {
        return serverErrorMessage;
    }
}
