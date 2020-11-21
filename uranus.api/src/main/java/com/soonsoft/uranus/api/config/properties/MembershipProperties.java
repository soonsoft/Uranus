package com.soonsoft.uranus.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MembershipProperties
 */
@ConfigurationProperties("membership")
public class MembershipProperties {

    private String defaultPassword;

    private String salt;

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}