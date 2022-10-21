package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class ApprovalCheckParameter extends FlowActionParameter {

    /** 审批记录 ID */
    private Object approvalId;
    /** 审批记录编号 */
    private String recordCode;
    /** 审批动作编号 */
    private String actionCode;
    /** 审批备注 */
    private String remark;

    public Object getApprovalId() {
        return approvalId;
    }
    public void setApprovalId(Object approvalId) {
        this.approvalId = approvalId;
    }

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
