package com.soonsoft.uranus.security.entity;

public interface StatusConst {
    
    public interface UserStatus {
        String ENABLED = "ENABLED";
        String DISABLED = "DISABLED";
        String LOCKED = "LOCKED";
        String EXPIRED = "EXPIRED";
    }

    public interface PasswordStatus {
        String ENABLED = "ENABLED";
        String DISABLED = "DISABLED";
    }

    public interface PasswordType {
        String INITIAL = "INITIAL";
        String NORMAL = "NORMAL";
    }

    public interface RoleStatus {
        String ENABLED = "ENABLED";
        String DISABLED = "DISABLED";
    }
}
