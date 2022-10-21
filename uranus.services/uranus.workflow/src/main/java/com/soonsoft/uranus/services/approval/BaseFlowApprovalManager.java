package com.soonsoft.uranus.services.approval;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckResult;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFLowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class BaseFlowApprovalManager implements IApprovalManager {

    private StateMachineFlowFactory<Object> flowFactory;

    public BaseFlowApprovalManager() {
        // define constructors
    }

    @Override
    public ApprovalRecord submit(ApprovalCreateParameter parameter) {
        Guard.notNull(parameter, "the parameter parameter is required.");

        ApprovalRecord record = createApprovalRecord(parameter);
        if(record == null) {
            throw new ApprovalException("cannot create approval record.");
        }

        ApprovalHistoryRecord historyRecord = new ApprovalHistoryRecord();
        historyRecord.setOperator(parameter.getOperator());
        historyRecord.setOperatorName(parameter.getOperatorName());
        historyRecord.setOperateTime(parameter.getOperateTime());

        StateMachineFlowDefinition definition = getFlowDefinition(record.getApprovalType());
        if(definition == null) {
            throw new ApprovalException("cannot find definition by type[%s]", record.getApprovalType());
        }

        StateMachineFLowEngine<Object> flowEngine = getFlowFactory().createEngine(definition);
        flowEngine.start(new ApprovalRecordHolder(record, historyRecord));

        return record;
    }

    @Override
    public ApprovalRecord resubmit() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ApprovalRecord revoke() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ApprovalCheckResult check(ApprovalCheckParameter parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        
    }

    public StateMachineFlowFactory<Object> getFlowFactory() {
        return flowFactory;
    }

    protected void initFlowFactory() {
        DefaultApprovalStateMachineFlowRepository repository = new DefaultApprovalStateMachineFlowRepository();
        flowFactory = new StateMachineFlowFactory<Object>(repository, null);
    }

    protected ApprovalRecord createApprovalRecord(ApprovalCreateParameter parameter) {
        ApprovalRecord record = new ApprovalRecord();
        // TODO: create approval record instance
        return record;
    }

    protected StateMachineFlowDefinition getFlowDefinition(String approvalType) {
        return null;
    }

    public static class ApprovalRecordHolder extends FlowActionParameter {

        private ApprovalRecord record;

        private ApprovalHistoryRecord historyRecord;

        public ApprovalRecordHolder(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
            this.record = record;
            this.historyRecord = historyRecord;

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

    }

    public static class DefaultApprovalStateMachineFlowRepository 
            implements IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {

        @Override
        public StateMachineFlowDefinition getDefinition(Object parameter) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public FlowNode<?> getCurrentNode() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public StateMachineFlowState getCurrentState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void create(StateMachineFlowDefinition definition, FlowActionParameter parameter) {
            ApprovalRecordHolder recordHolder = (ApprovalRecordHolder) parameter;

            ApprovalRecord record = recordHolder.getRecord();
            StateMachineFlowState state = definition.getCurrentState();
            record.setFlowState(state);
            record.setStatus(ApprovalStatus.Checking);
            
            ApprovalHistoryRecord historyRecord = recordHolder.getHistoryRecord();

            // TODO： 保存数据
            
        }

        @Override
        public void saveState(StateMachineFlowState stateParam, FlowActionParameter parameter) {
            // TODO Auto-generated method stub
            
        }

    }

    
    
}
