package com.soonsoft.uranus.services.approval.simple;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.IApprovalManager;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.approval.model.ApprovalTargetInfo;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFLowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class SimpleApprovalManager<TApprovalQuery> implements IApprovalManager<TApprovalQuery> {

    private final StateMachineFlowFactory<TApprovalQuery> flowFactory;
    private final TApprovalQuery queryObject;
    private final IApprovalRepository approvalRepository;
    private final Func0<String> codeGenerator;
    private final Func1<String, StateMachineFlowDefinition> definitionGetter;

    public SimpleApprovalManager(
            TApprovalQuery query, 
            IApprovalRepository approvalRepository,
            Func0<String> codeGenerator,
            Func1<String, StateMachineFlowDefinition> definitionGetter) {

        this.queryObject = query;
        this.approvalRepository = approvalRepository;
        this.codeGenerator = codeGenerator;
        this.definitionGetter = definitionGetter;
        this.flowFactory = createFlowFactory();
    }

    @Override
    public ApprovalRecord submit(ApprovalCreateParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");

        ApprovalRecord record = createApprovalRecord(parameter);
        StateMachineFlowDefinition definition = getFlowDefinition(record.getApprovalType());
        record.setApprovalFlowCode(definition.getFlowCode());

        StateMachineFLowEngine<TApprovalQuery> flowEngine = getFlowFactory().createEngine(definition);
        // 流程开始
        flowEngine.start();
        
        record.setStatus(ApprovalStatus.Checking);
        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, ApprovalActionType.Submit);
        // 流程提交
        StateMachineFlowState actionState = flowEngine.action(
            definition.getCurrentNodeCode(), 
            ApprovalStateCode.Checking, 
            new ApprovalRecordHolder(record, historyRecord));

        record.setFlowState(actionState);
        record.addHistoryRecord(historyRecord);

        return record;
    }

    @Override
    public ApprovalRecord resubmit(ApprovalParameter parameter) {
        checkParameter(parameter);

        return doAction(ApprovalActionType.Resubmit, ApprovalStateCode.Checking, parameter);
    }

    @Override
    public ApprovalRecord revoke(ApprovalParameter parameter) {
        checkParameter(parameter);
        Guard.notEmpty("nodeCode", "the parameter nodeCode is required.");

        final String recordCode = parameter.getRecordCode();

        ApprovalRecord record = approvalRepository.getApprovalRecord(recordCode);
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", recordCode);
        }

        StateMachineFlowDefinition definition = flowFactory.loadDefinition(record);
        if(definition == null) {
            throw new ApprovalException("cannot load definition");
        }

        StateMachineFlowState currentState = record.getFlowState();
        String previousNodeCode = currentState.getNodeCode();
        StateMachineFlowNode previousNode = definition.findNode(currentState.getNodeCode());
        if(!previousNode.isBeginNode()) {
            throw new ApprovalException("the previousNodeCode[%s] can not support revoke.", previousNodeCode);
        }

        StateMachineFLowEngine<TApprovalQuery> flowEngine = flowFactory.createEngine(definition);
        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, ApprovalActionType.Revoke, hr -> {
                hr.setRemark(parameter.getRemark());
                hr.setPreviousHistoryId(record.getCurrentHistoryId());
            });
        record.setCurrentHistoryId(historyRecord.getId());

        final String currentNodeCode = definition.getCurrentNodeCode();
        final String itemCode = parameter instanceof IPartialItemCode pic ? pic.getItemCode() : null;
        StateMachineFlowState actionState = 
            flowEngine.action(currentNodeCode, ApprovalStateCode.Revoked, new ApprovalRecordHolder(record, historyRecord, itemCode));
        record.setFlowState(actionState);

        return record;
    }

    @Override
    public ApprovalRecord check(ApprovalCheckParameter parameter) {
        checkParameter(parameter);
        Guard.notEmpty(parameter.getStateCode(), "the parameter.actionCode is required.");

        return doAction(ApprovalActionType.Check, parameter.getStateCode(), parameter);
    }

    @Override
    public void cancel(ApprovalParameter parameter) {
        checkParameter(parameter);

        ApprovalRecord record = approvalRepository.getApprovalRecord(parameter.getRecordCode());
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", parameter.getRecordCode());
        }

        StateMachineFlowDefinition definition = flowFactory.loadDefinition(record);
        if(definition == null) {
            throw new ApprovalException("cannot load definition");
        }

        // TODO: 复合节点如何处理取消？
        
        record.setStatus(ApprovalStatus.Canceled);
        StateMachineFLowEngine<TApprovalQuery> flowEngine = flowFactory.createEngine(definition);
        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, ApprovalActionType.Cancel, hr -> {
                hr.setRemark(parameter.getRemark());
                hr.setPreviousHistoryId(record.getCurrentHistoryId());
            });
        record.setCurrentHistoryId(historyRecord.getId());
        flowEngine.cancel(new ApprovalRecordHolder(record, historyRecord));
    }

    @Override
    public ApprovalRecord getApprovalRecord(String recordCode) {
        Guard.notEmpty(recordCode, "the parameter recordCode is required.");
        return approvalRepository.getApprovalRecord(recordCode);
    }

    @Override
    public TApprovalQuery query() {
        return queryObject;
    }

    public StateMachineFlowFactory<TApprovalQuery> getFlowFactory() {
        return flowFactory;
    }

    protected StateMachineFlowDefinition getFlowDefinition(String approvalType) {
        StateMachineFlowDefinition definition = definitionGetter.call(approvalType);
        if(definition == null) {
            throw new ApprovalException("cannot find definition by type[%s]", approvalType);
        }
        return definition;
    }

    protected StateMachineFlowFactory<TApprovalQuery> createFlowFactory() {
        SimpleApprovalStateMachineFlowRepository repository = 
            new SimpleApprovalStateMachineFlowRepository(approvalRepository, this::getFlowDefinition);
        return new StateMachineFlowFactory<TApprovalQuery>(repository, queryObject);
    }

    protected ApprovalRecord createApprovalRecord(ApprovalCreateParameter parameter) {
        ApprovalRecord record = fillApprovalRecord(new ApprovalRecord(), parameter);

        if(!CollectionUtils.isEmpty(parameter.getSubList())) {
            for(ApprovalCreateParameter subParameter : parameter.getSubList()) {
                ApprovalRecord subRecord = fillApprovalRecord(new ApprovalRecord(), subParameter);
                subRecord.setParentRecordCode(record.getRecordCode());
                record.addSubRecord(subRecord);
            }
        }
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

    protected ApprovalRecord doAction(ApprovalActionType actionType, String stateCode, ApprovalParameter parameter) {
        final String recordCode = parameter.getRecordCode();

        ApprovalRecord record = approvalRepository.getApprovalRecord(recordCode);
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", recordCode);
        }

        StateMachineFlowDefinition definition = flowFactory.loadDefinition(record);
        if(definition == null) {
            throw new ApprovalException("cannot load definition");
        }

        StateMachineFLowEngine<TApprovalQuery> flowEngine = flowFactory.createEngine(definition);
        final String nodeCode = definition.getCurrentNodeCode();
        ApprovalHistoryRecord historyRecord = 
            createApprovalHistoryRecord(parameter, record, actionType, hr -> {
                hr.setRemark(parameter.getRemark());
                hr.setPreviousHistoryId(record.getCurrentHistoryId());
            });
        record.setCurrentHistoryId(historyRecord.getId());

        final String itemCode = parameter instanceof IPartialItemCode pic ? pic.getItemCode() : null;
        StateMachineFlowState actionState = 
            flowEngine.action(nodeCode, stateCode, new ApprovalRecordHolder(record, historyRecord, itemCode));
        if(actionState instanceof CompositionPartialState partialState) {
            actionState = definition.createFlowState();
            actionState.setNodeCode(definition.getPreviousNodeCode());
            actionState.setStateCode(definition.getPreviousStateCode());
            actionState.setToNodeCode(definition.getCurrentNodeCode());
        }
        record.setFlowState(actionState);
        
        if(flowEngine.isFinished()) {
            record.setStatus(ApprovalStatus.Completed);
        }

        return record;
    }

    private ApprovalRecord fillApprovalRecord(ApprovalRecord record, ApprovalCreateParameter parameter) {
        record.setRecordCode(codeGenerator.call());
        record.setApprovalType(parameter.getApprovalType());
        record.setSource(parameter.getSource());

        ApprovalTargetInfo targetInfo = new ApprovalTargetInfo();
        targetInfo.setBusinessCode(parameter.getBusinessCode());
        targetInfo.setEntityCode(parameter.getEntityCode());
        targetInfo.setEntityId(parameter.getEntityId());
        if(CollectionUtils.isEmpty(parameter.getDataList())) {
            throw new IllegalArgumentException("parameter.dataList is empty.");
        }
        targetInfo.setDataList(parameter.getDataList());
        record.setTargetInfo(targetInfo);

        return record;
    }

    private void checkParameter(ApprovalParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");
        Guard.notEmpty(parameter.getRecordCode(), "the parameter.recordCode is required.");
    }

}
