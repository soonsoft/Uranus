package com.soonsoft.uranus.services.approval.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核记录
 */
public class ApprovalRecord {

    /** 审核记录 ID */
    private Object id;
    /** 审核记录编号（唯一） */
    private String recordCode;
    /** 父级审核记录编号（只有子审核单才有） */
    private Object parentRecordCode;
    /** 审核流程编号 */
    private String approvalFlowCode;
    /** 子审核单列表 */
    private List<ApprovalRecord> subApprovalRecordList;
    /** 审核类型 */
    private String approvalType;
    /** 审核记录来源 */
    private String source;
    /** 审核目标信息 */
    private ApprovalTargetInfo targetInfo;
    /** 审核记录状态 */
    private ApprovalStatus status;
    /** 审核记录提交历史 ID */
    private Object beginHistoryId;
    /** 审核记录当前操作历史 ID */
    private Object currentHistoryId;
    /** 【流程状态】 - 前一个节点编号 */
    private String previousNodeCode;
    /** 【流程状态】 - 前一个状态编号 */
    private String previousStateCode;
    /** 【流程状态】 - 当前节点编号 */
    private String currentNodeCode;
    /** 复合节点与并行节点，用于关联 PartialItem 数据 */
    private String currentNodeMark;
    /** 审核历史列表 */
    private List<ApprovalHistoryRecord> historyRecordList;
    /** 提交记录（查询时填充） */
    private ApprovalHistoryRecord beginHistoryRecord;
    /** 当前审核记录 */
    private ApprovalHistoryRecord currentHistoryRecord;

    public Object getId() {
        return id;
    }
    public void setId(Object id) {
        this.id = id;
    }

    public String getRecordCode() {
        return recordCode;
    }
    public void setRecordCode(String recordCode) {
        this.recordCode = recordCode;
    }

    public Object getParentRecordCode() {
        return parentRecordCode;
    }
    public void setParentRecordCode(Object parentRecordCode) {
        this.parentRecordCode = parentRecordCode;
    }

    public String getApprovalFlowCode() {
        return approvalFlowCode;
    }
    public void setApprovalFlowCode(String approvalFlowCode) {
        this.approvalFlowCode = approvalFlowCode;
    }

    public List<ApprovalRecord> getSubApprovalRecordList() {
        return subApprovalRecordList;
    }
    public void setSubApprovalRecordList(List<ApprovalRecord> subApprovalRecordList) {
        this.subApprovalRecordList = subApprovalRecordList;
    }
    public boolean addSubRecord(ApprovalRecord subRecord) {
        if(subRecord != null) {
            if(subApprovalRecordList == null) {
                subApprovalRecordList = new ArrayList<>();
            }
            subApprovalRecordList.add(subRecord);
        }
        return false;
    }

    public String getApprovalType() {
        return approvalType;
    }
    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public ApprovalTargetInfo getTargetInfo() {
        return targetInfo;
    }
    public void setTargetInfo(ApprovalTargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public String getPreviousNodeCode() {
        return previousNodeCode;
    }
    public void setPreviousNodeCode(String previousNodeCode) {
        this.previousNodeCode = previousNodeCode;
    }

    public String getPreviousStateCode() {
        return previousStateCode;
    }
    public void setPreviousStateCode(String previousStateCode) {
        this.previousStateCode = previousStateCode;
    }

    public String getCurrentNodeCode() {
        return currentNodeCode;
    }
    public void setCurrentNodeCode(String currentNodeCode) {
        this.currentNodeCode = currentNodeCode;
    }
    
    public ApprovalStatus getStatus() {
        return status;
    }
    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public Object getBeginHistoryId() {
        return beginHistoryId;
    }
    public void setBeginHistoryId(Object beginHistoryId) {
        this.beginHistoryId = beginHistoryId;
    }

    public Object getCurrentHistoryId() {
        return currentHistoryId;
    }
    public void setCurrentHistoryId(Object currentHistoryId) {
        this.currentHistoryId = currentHistoryId;
    }

    public String getCurrentNodeMark() {
        return currentNodeMark;
    }
    public void setCurrentNodeMark(String compositionActionCode) {
        this.currentNodeMark = compositionActionCode;
    }

    public ApprovalHistoryRecord getBeginHistoryRecord() {
        return beginHistoryRecord;
    }
    public void setBeginHistoryRecord(ApprovalHistoryRecord beginHistoryRecord) {
        this.beginHistoryRecord = beginHistoryRecord;
    }

    public ApprovalHistoryRecord getCurrentHistoryRecord() {
        return currentHistoryRecord;
    }
    public void setCurrentHistoryRecord(ApprovalHistoryRecord currentHistoryRecord) {
        this.currentHistoryRecord = currentHistoryRecord;
    }

    public List<ApprovalHistoryRecord> getHistoryRecordList() {
        return historyRecordList;
    }
    public void setHistoryRecordList(List<ApprovalHistoryRecord> historyRecordList) {
        this.historyRecordList = historyRecordList;
    }
    public boolean addHistoryRecord(ApprovalHistoryRecord historyRecord) {
        if(historyRecord != null) {
            if(this.historyRecordList == null) {
                this.historyRecordList = new ArrayList<>();
            }
            return this.historyRecordList.add(historyRecord);
        }
        return false;
    }
    
}
