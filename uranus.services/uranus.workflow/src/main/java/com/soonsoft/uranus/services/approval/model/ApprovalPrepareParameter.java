package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class ApprovalPrepareParameter extends FlowActionParameter {

    /** 审批记录编号 */
    private String recordCode;

    public String getRecordCode() {
        return recordCode;
    }
    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }
    
}
