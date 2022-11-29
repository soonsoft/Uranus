package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class ApprovalParameter extends FlowActionParameter {

    /** 审批记录编号 */
    private String recordCode;
    /** 备注 */
    private String remark;

    public String getRecordCode() {
        return recordCode;
    }
    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
