package com.soonsoft.uranus.services.approval;

import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalPrepareParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.IStateMachineFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFLowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class DefaultApprovalManager<TApprovalQuery> implements IApprovalManager<TApprovalQuery> {

    private StateMachineFlowFactory<TApprovalQuery> flowFactory;
    private TApprovalQuery queryObject;
    private IApprovalBuilder builder;
    private IApprovalRepository approvalRepository;

    public DefaultApprovalManager(TApprovalQuery query) {
        initFlowFactory();
        queryObject = query;
    }

    @Override
    public ApprovalRecord submit(ApprovalCreateParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");

        ApprovalRecord record = createApprovalRecord(parameter);
        if(record == null) {
            throw new ApprovalException("cannot create approval record.");
        }

        StateMachineFlowDefinition definition = getFlowDefinition(record.getApprovalType());
        if(definition == null) {
            throw new ApprovalException("cannot find definition by type[%s]", record.getApprovalType());
        }

        StateMachineFLowEngine<TApprovalQuery> flowEngine = getFlowFactory().createEngine(definition);
        // 流程开始
        flowEngine.start();

        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, ApprovalActionType.Submit);
        // 流程提交
        StateMachineFlowState actionState = flowEngine.action(
            definition.getCurrentNodeCode(), 
            IApprovalManager.ActionType.Submit, 
            new ApprovalRecordHolder(record, historyRecord));

        record.setFlowState(actionState);
        record.setStatus(ApprovalStatus.Checking);
        record.addHistoryRecord(historyRecord);

        return record;
    }

    @Override
    public ApprovalRecord resubmit(ApprovalPrepareParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");
        Guard.notNull(parameter.getRecordCode(), "the parameter.recordCode is required.");
        return null;
    }

    @Override
    public ApprovalRecord revoke(ApprovalPrepareParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");
        Guard.notNull(parameter.getRecordCode(), "the parameter.recordCode is required.");
        return null;
    }

    @Override
    public ApprovalRecord check(ApprovalCheckParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");
        Guard.notEmpty(parameter.getRecordCode(), "the parameter.recordCode is required.");
        Guard.notEmpty(parameter.getActionCode(), "the parameter.actionCode is required.");

        ApprovalRecord record = approvalRepository.getApprovalRecord(parameter.getRecordCode());
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", parameter.getRecordCode());
        }

        StateMachineFlowDefinition definition = flowFactory.loadDefinition(record);
        if(definition == null) {
            throw new ApprovalException("cannot load definition");
        }

        StateMachineFLowEngine<TApprovalQuery> flowEngine = flowFactory.createEngine(definition);
        final String nodeCode = definition.getCurrentNodeCode();
        final String actionCode = parameter.getActionCode();
        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, ApprovalActionType.Check, hr -> {
                hr.setRemark(parameter.getRemark());
                hr.setPreviousHistoryId(record.getCurrentHistoryId());
            });
        StateMachineFlowState actionState = 
            flowEngine.action(nodeCode, actionCode, new ApprovalRecordHolder(record, historyRecord));

        record.setCurrentHistoryId(historyRecord.getId());
        record.setFlowState(actionState);
        
        if(flowEngine.isFinished()) {
            record.setStatus(ApprovalStatus.Completed);
        }

        return record;
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public TApprovalQuery query() {
        return queryObject;
    }

    public StateMachineFlowFactory<TApprovalQuery> getFlowFactory() {
        return flowFactory;
    }

    protected void initFlowFactory() {
        DefaultApprovalStateMachineFlowRepository repository = 
            new DefaultApprovalStateMachineFlowRepository(approvalRepository, this::getFlowDefinition);
        flowFactory = new StateMachineFlowFactory<TApprovalQuery>(repository, null);
    }

    protected ApprovalRecord createApprovalRecord(ApprovalCreateParameter parameter) {
        ApprovalRecord record = new ApprovalRecord();
        // TODO: create approval record instance
        return record;
    }

    protected ApprovalHistoryRecord createApprovalHistoryRecord(
            FlowActionParameter parameter, 
            ApprovalRecord record, 
            ApprovalActionType actionType) {
        return createApprovalHistoryRecord(parameter, record, actionType, null);
    }

    protected ApprovalHistoryRecord createApprovalHistoryRecord(
            FlowActionParameter parameter, 
            ApprovalRecord record, 
            ApprovalActionType actionType,
            Action1<ApprovalHistoryRecord> historyRecordSetterAction) {
        
        ApprovalHistoryRecord historyRecord = new ApprovalHistoryRecord();
        historyRecord.setOperator(parameter.getOperator());
        historyRecord.setOperatorName(parameter.getOperatorName());
        historyRecord.setOperateTime(parameter.getOperateTime());
        historyRecord.setApprovalRecordCode(record.getRecordCode());
        historyRecord.setHistoryRecordType(actionType);

        if(historyRecordSetterAction != null) {
            historyRecordSetterAction.apply(historyRecord);
        }

        return historyRecord;
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
            implements IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {

        private Func1<String, StateMachineFlowDefinition> findFlowDefinitionFn;
        private IApprovalRepository approvalRepository;

        public DefaultApprovalStateMachineFlowRepository(
                IApprovalRepository approvalRepository, 
                Func1<String, StateMachineFlowDefinition> findFlowDefinitionFn) {
            this.approvalRepository = approvalRepository;
            this.findFlowDefinitionFn = findFlowDefinitionFn;
        }

        @Override
        public StateMachineFlowDefinition getDefinition(String flowCode) {
            return findFlowDefinitionFn != null ? findFlowDefinitionFn.call(flowCode) : null;
        }

        @Override
        public StateMachineFlowState getCurrentState(Object parameter) {
            String recordCode;
            if(parameter instanceof ApprovalRecord record) {
                return record.getFlowState();
            } else if(parameter instanceof ApprovalCheckParameter p) {
                recordCode = p.getRecordCode();
            } else if(parameter instanceof String code) {
                recordCode = code;
            } else {
                throw new ApprovalException("unknown parameter type.");
            }
            ApprovalRecord record = approvalRepository.getApprovalRecord(recordCode);
            return record.getFlowState();
        }

        @Override
        public void create(StateMachineFlowDefinition definition, FlowActionParameter parameter) {
            // 不做处理
        }

        @Override
        public void saveState(StateMachineFlowState stateParam, FlowActionParameter parameter) {
            ApprovalRecordHolder recordHolder = (ApprovalRecordHolder) parameter;

            ApprovalRecord record = recordHolder.getRecord();
            record.setFlowState(stateParam);
            record.setStatus(ApprovalStatus.Checking);
            
            ApprovalHistoryRecord historyRecord = recordHolder.getHistoryRecord();
            historyRecord.setFlowState(stateParam);

            approvalRepository.saveChecking(record, historyRecord);
        }

        @Override
        public List<StateMachinePartialItem> getPratialItems(StateMachineCompositeNode compositeNode) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
