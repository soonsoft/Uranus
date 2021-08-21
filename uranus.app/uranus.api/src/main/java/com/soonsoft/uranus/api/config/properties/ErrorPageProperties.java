package com.soonsoft.uranus.api.config.properties;

import com.soonsoft.uranus.web.error.properties.ErrorMessageProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "uranus.error")
public class ErrorPageProperties extends ErrorMessageProperties {
}
