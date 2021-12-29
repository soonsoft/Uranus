package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "auth_privilege")
public class AuthPrivilege {
    
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "function_id")
    private UUID functionId;

    private String functionName;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getFunctionId() {
        return functionId;
    }

    public void setFunctionId(UUID functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

}
