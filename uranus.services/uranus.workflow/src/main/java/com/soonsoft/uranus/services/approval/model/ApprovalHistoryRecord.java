package com.soonsoft.uranus.services.approval.model;

import java.util.Date;

import com.soonsoft.uranus.services.workflow.model.FlowState;

public class ApprovalHistoryRecord {
    
    /** 审核历史记录 ID */
    private Object id;
    /** 审核记录编号 */
    private String approvalRecordCode;
    /** 审核历史记录类型 */
    private ApprovalActionType historyRecordType;
    /** 操作人 */
    private String operator;
    /** 操作人姓名 */
    private String operatorName;
    /** 操作时间 */
    private Date operateTime;
    /** 审核备注 */
    private String remark;
    /** 前一个历史记录 ID */
    private String previousHistoryId;
    /** 历史状态信息 */
    private FlowState flowState;

    public Object getId() {
        return id;
    }
    public void setId(Object id) {
        this.id = id;
    }

    public String getApprovalRecordCode() {
        return approvalRecordCode;
    }
    public void setApprovalRecordCode(String approvalRecordCode) {
        this.approvalRecordCode = approvalRecordCode;
    }

    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorName() {
        return operatorName;
    }
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getOperateTime() {
        return operateTime;
    }
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPreviousHistoryId() {
        return previousHistoryId;
    }
    public void setPreviousHistoryId(String previousHistoryId) {
        this.previousHistoryId = previousHistoryId;
    }

    public FlowState getFlowState() {
        return flowState;
    }
    public void setFlowState(FlowState flowState) {
        this.flowState = flowState;
    }

}
