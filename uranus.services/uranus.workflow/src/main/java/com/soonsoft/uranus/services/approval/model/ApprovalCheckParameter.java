package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

/**
 * 审核参数
 */
public class ApprovalCheckParameter extends FlowActionParameter {

    /** 审批记录编号 */
    private String recordCode;
    /** 审批动作编号 */
    private String actionCode;
    /** 审批备注 */
    private String remark;

    public String getRecordCode() {
        return recordCode;
    }
    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getActionCode() {
        return actionCode;
    }
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
