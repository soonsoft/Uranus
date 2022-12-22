package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class ApprovalParameter extends FlowActionParameter {

    /** 审批记录编号 */
    private String recordCode;
    /** 当前操作的节点编号 */
    private String actionNodeCode;
    /** 备注 */
    private String remark;

    public String getRecordCode() {
        return recordCode;
    }
    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public String getActionNodeCode() {
        return actionNodeCode;
    }
    public void setActionNodeCode(String actionNodeCode) {
        this.actionNodeCode = actionNodeCode;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}
