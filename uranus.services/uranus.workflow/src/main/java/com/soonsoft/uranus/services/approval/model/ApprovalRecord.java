package com.soonsoft.uranus.services.approval.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;

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
    /** 复合节点与并行节点，用于关联 PartialItem 数据 */
    private String currentNodeMark;
    /** 审核历史列表 */
    private List<ApprovalHistoryRecord> historyRecordList;

    /** 审核流程状态信息(不被序列化) */
    private transient StateMachineFlowState flowState;

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

    @Transient
    public StateMachineFlowState getFlowState() {
        return flowState;
    }
    public void setFlowState(StateMachineFlowState flowState) {
        this.flowState = flowState;
    }

    public String getCurrentNodeCode() {
        return flowState != null ? flowState.getToNodeCode() : null;
    }
    public String getPreviousNodeCode() {
        return flowState != null ? flowState.getNodeCode() : null;
    }
    public String getPreviousStateCode() {
        return flowState != null ? flowState.getStateCode() : null;
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
