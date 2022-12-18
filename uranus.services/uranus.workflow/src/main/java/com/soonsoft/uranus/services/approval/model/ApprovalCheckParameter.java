package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;

/**
 * 审核参数
 */
public class ApprovalCheckParameter extends ApprovalParameter implements IPartialItemCode {

    /** 审批动作编号 */
    private String stateCode;
    /** 会签、或签的项目编号 */
    private String itemCode;

    public String getStateCode() {
        return stateCode;
    }
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    @Override
    public String getItemCode() {
        return itemCode;
    }
    
}
