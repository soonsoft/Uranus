package com.soonsoft.uranus.services.approval.model;

public class ApprovalData {

    /** 审核数据 ID */
    private Object id;
    /** 提交历史纪录标识，区分多次提交的不同数据 */
    private Object submitHistoryId;
    /** 审核目标类型（标识 targetID 与 originalID */
    private String targetType;
    /** 审核目标数据 ID */
    private Object targetId;
    /** 审核目标原始数据 ID（数据变更时保留原始数据） */
    private Object originalId;

    public Object getId() {
        return id;
    }
    public void setId(Object id) {
        this.id = id;
    }

    public Object getSubmitHistoryId() {
        return submitHistoryId;
    }
    public void setSubmitHistoryId(Object submitHistoryId) {
        this.submitHistoryId = submitHistoryId;
    }

    public String getTargetType() {
        return targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public Object getTargetId() {
        return targetId;
    }
    public void setTargetId(Object targetId) {
        this.targetId = targetId;
    }

    public Object getOriginalId() {
        return originalId;
    }
    public void setOriginalId(Object originalId) {
        this.originalId = originalId;
    }
    
}
