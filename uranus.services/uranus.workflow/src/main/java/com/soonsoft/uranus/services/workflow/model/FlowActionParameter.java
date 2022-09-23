package com.soonsoft.uranus.services.workflow.model;

import java.util.Date;

public class FlowActionParameter {
    
    private String operator;

    private Date operateTime;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

}
