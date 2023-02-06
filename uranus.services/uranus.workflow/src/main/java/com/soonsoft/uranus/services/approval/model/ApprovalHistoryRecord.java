package com.soonsoft.uranus.services.approval.model;

import java.util.Date;

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
    private Object previousHistoryId;
    /** 当前操作节点 */
    private String nodeCode;
    /** 当前操作状态 */
    private String stateCode;
    /** 到达节点 */
    private String toNodeCode;
    /** 复合节点与并行节点，用于关联 PartialItem 数据 */
    private String currentNodeMark;
    /** PartialItem 当前操作项 */
    private String itemCode;
    /** PartialItem 当前操作状态 */
    private String itemStateCode;

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
    
    public ApprovalActionType getHistoryRecordType() {
        return historyRecordType;
    }
    public void setHistoryRecordType(ApprovalActionType historyRecordType) {
        this.historyRecordType = historyRecordType;
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

    public Object getPreviousHistoryId() {
        return previousHistoryId;
    }
    public void setPreviousHistoryId(Object previousHistoryId) {
        this.previousHistoryId = previousHistoryId;
    }

    public String getNodeCode() {
        return nodeCode;
    }
    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getStateCode() {
        return stateCode;
    }
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getToNodeCode() {
        return toNodeCode;
    }
    public void setToNodeCode(String toNodeCode) {
        this.toNodeCode = toNodeCode;
    }

    public String getCurrentNodeMark() {
        return currentNodeMark;
    }
    public void setCurrentNodeMark(String compositionActionCode) {
        this.currentNodeMark = compositionActionCode;
    }

    public String getItemCode() {
        return itemCode;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemStateCode() {
        return itemStateCode;
    }
    public void setItemStateCode(String itemStateCode) {
        this.itemStateCode = itemStateCode;
    }
}
