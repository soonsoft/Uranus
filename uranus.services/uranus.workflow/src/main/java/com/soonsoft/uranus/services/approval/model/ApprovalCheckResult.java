package com.soonsoft.uranus.services.approval.model;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;

public class ApprovalCheckResult extends ApprovalRecord {

    /** 审批流程定义 */
    private StateMachineFlowDefinition approvalFlowDefinition;
    /** 审批流程提交历史记录 */
    private ApprovalHistoryRecord beginHistoryRecord;
    /** 当前审批历史记录 */
    private ApprovalHistoryRecord currentHistoryRecord;
    
}
