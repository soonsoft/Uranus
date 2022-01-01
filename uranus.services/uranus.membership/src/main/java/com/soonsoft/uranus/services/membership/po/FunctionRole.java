package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;

public class FunctionRole extends AuthRole {

    private UUID functionId;

    public UUID getFunctionId() {
        return functionId;
    }

    public void setFunctionId(UUID functionId) {
        this.functionId = functionId;
    }
}
