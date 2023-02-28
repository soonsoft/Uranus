package com.soonsoft.uranus.services.approval.simple;

import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

final class ApprovalRecordHolder extends FlowActionParameter implements IPartialItemCode  {

    private final String itemCode;
    private final ApprovalRecord record;
    private final ApprovalHistoryRecord historyRecord;
    private final Func0<StateMachineFlowDefinition> currentDefinitionGetter;

    public ApprovalRecordHolder(
        ApprovalRecord record, 
        ApprovalHistoryRecord historyRecord,
        Func0<StateMachineFlowDefinition> currentDefinitionGetter) {
        this(record, historyRecord, currentDefinitionGetter, null);
    }

    public ApprovalRecordHolder(
            ApprovalRecord record, 
            ApprovalHistoryRecord historyRecord, 
            Func0<StateMachineFlowDefinition> currentDefinitionGetter, 
            String itemCode) {

        this.record = record;
        this.historyRecord = historyRecord;
        this.currentDefinitionGetter = currentDefinitionGetter;
        this.itemCode = itemCode;

        this.setOperator(historyRecord.getOperator());
        this.setOperatorName(historyRecord.getOperatorName());
        this.setOperateTime(historyRecord.getOperateTime());
    }

    public ApprovalRecord getRecord() {
        return record;
    }

    public ApprovalHistoryRecord getHistoryRecord() {
        return historyRecord;
    }

    public StateMachineFlowDefinition getCurrentDefinition() {
        return currentDefinitionGetter != null ? currentDefinitionGetter.call() : null;
    }

    @Override
    public String getItemCode() {
        return itemCode;
    }
    
}
