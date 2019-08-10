package com.soonsoft.uranus.services.membership.dto;

/**
 * AuthRoleIdAndFunctionId
 */
public class AuthRoleIdAndFunctionId extends AuthRole {

    private String functionId;

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }
    
}