package com.soonsoft.uranus.services.approval.model;

import java.util.List;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class ApprovalCreateParameter extends FlowActionParameter {
    
    /** 审核类型 */
    private String approvalType;
    /** 审核记录来源 */
    private String source;
    /** 业务编号 */
    private String businessCode;
    /** 业务实体编号（被审核对象主体） */
    private String entityCode;
    /** 业务实体 ID （被审核对象主体 ID） */
    private Object entityId;
    /** 业务实体对应的数据列表（一个实体可能由多条数据构成） */
    private List<ApprovalData> dataList;
    /** 审核目标信息 */
    private ApprovalTargetInfo targetInfo;
    /** 子审核单列表 */
    private List<ApprovalCreateParameter> subList;

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
    
    //public void addSubItem(Appr String source)
    
}
