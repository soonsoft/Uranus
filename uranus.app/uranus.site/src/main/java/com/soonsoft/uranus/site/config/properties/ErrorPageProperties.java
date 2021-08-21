package com.soonsoft.uranus.site.config.properties;

import com.soonsoft.uranus.web.error.properties.ErrorMessageProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.error")
public class ErrorPageProperties extends ErrorMessageProperties {

    private String badRequestPage;

    private String unauthorizedPage;

    private String forbiddenPage;

    private String notFoundPage;

    private String serverErrorPage;

    public String getBadRequestPage() {
        return badRequestPage;
    }

    public String getUnauthorizedPage() {
        return unauthorizedPage;
    }

    public String getForbiddenPage() {
        return forbiddenPage;
    }

    public String getNotFoundPage() {
        return notFoundPage;
    }

    public String getServerErrorPage() {
        return serverErrorPage;
    }
}
