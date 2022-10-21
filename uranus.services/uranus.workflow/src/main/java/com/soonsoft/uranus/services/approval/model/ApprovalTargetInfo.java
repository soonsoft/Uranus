package com.soonsoft.uranus.services.approval.model;

import java.util.List;

public class ApprovalTargetInfo {

    /** 业务编号 */
    private String businessCode;
    /** 业务实体编号（被审核对象主体） */
    private String entityCode;
    /** 业务实体 ID （被审核对象主体 ID） */
    private Object entityId;
    /** 业务实体对应的数据列表（一个实体可能由多条数据构成） */
    private List<ApprovalData> dataList;

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

    public List<ApprovalData> getDataList() {
        return dataList;
    }
    public void setDataList(List<ApprovalData> dataList) {
        this.dataList = dataList;
    }
    
}
