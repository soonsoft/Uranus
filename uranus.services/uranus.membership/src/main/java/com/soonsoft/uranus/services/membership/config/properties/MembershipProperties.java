package com.soonsoft.uranus.services.membership.config.properties;

import com.soonsoft.uranus.security.config.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Value;

public class MembershipProperties extends SecurityProperties {

    @Value("${uranus.security.membership.default-password:1}")
    private String defaultPassword;

    @Value("${uranus.security.membership.salt:}")
    private String salt;

    @Value("${uranus.security.membership.database-access-name:masterAccess}")
    private String databaseAccessName;

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

    public String getDatabaseAccessName() {
        return databaseAccessName;
    }

    public void setMembershipDatabaseAccessName(String databaseAccessName) {
        this.databaseAccessName = databaseAccessName;
    }

}