package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "auth_roles_in_functions")
public class AuthRoleIdAndFunctionId extends AuthRole {

    @Column(name = "function_id")
    private UUID functionId;

    public UUID getFunctionId() {
        return functionId;
    }

    public void setFunctionId(UUID functionId) {
        this.functionId = functionId;
    }
    
}