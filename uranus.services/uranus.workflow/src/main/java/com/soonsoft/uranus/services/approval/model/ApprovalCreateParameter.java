package com.soonsoft.uranus.services.approval.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

/**
 * 提交审批流程参数
 */
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

    public String getBusinessCode() {
        return businessCode;
    }
    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getEntityCode() {
        return entityCode;
    }
    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public Object getEntityId() {
        return entityId;
    }
    public void setEntityId(Object entityId) {
        this.entityId = entityId;
    }

    public List<ApprovalCreateParameter> getSubList() {
        return subList;
    }
    public void setSubList(List<ApprovalCreateParameter> subList) {
        this.subList = subList;
    }
    public boolean addSubItem(ApprovalCreateParameter subItem) {
        if(subItem != null) {
            if(subList == null) {
                subList = new ArrayList<>();
            }
            return subList.add(subItem);
        }
        return false;
    }

    public List<ApprovalData> getDataList() {
        return dataList;
    }
    public void setDataList(List<ApprovalData> dataList) {
        this.dataList = dataList;
    }
    public boolean addApprovalData(ApprovalData data) {
        if(data != null) {
            if(dataList == null) {
                dataList = new ArrayList<>();
            }
            dataList.add(data);
        }
        return false;
    }
    
}
