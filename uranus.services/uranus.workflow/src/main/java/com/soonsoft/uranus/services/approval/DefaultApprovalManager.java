package com.soonsoft.uranus.services.approval;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.approval.model.ApprovalTargetInfo;
import com.soonsoft.uranus.services.workflow.engine.statemachine.IStateMachineFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFLowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.behavior.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class DefaultApprovalManager<TApprovalQuery> implements IApprovalManager<TApprovalQuery> {

    private StateMachineFlowFactory<TApprovalQuery> flowFactory;
    private TApprovalQuery queryObject;
    private IApprovalBuilder builder;
    private IApprovalRepository approvalRepository;
    private Func0<String> codeGenerator;

    public DefaultApprovalManager(TApprovalQuery query, Func0<String> codeGenerator) {
        initFlowFactory();
        this.queryObject = query;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public ApprovalRecord submit(ApprovalCreateParameter parameter) {
        Guard.notNull(parameter, "the arguments parameter is required.");

        ApprovalRecord record = createApprovalRecord(parameter);
        StateMachineFlowDefinition definition = getFlowDefinition(record.getApprovalType());
        if(definition == null) {
            throw new ApprovalException("cannot find definition by type[%s]", record.getApprovalType());
        }
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
            ApprovalActionType.Submit.name(), 
            new ApprovalRecordHolder(record, historyRecord));

        record.setFlowState(actionState);
        record.addHistoryRecord(historyRecord);

        return record;
    }

    @Override
    public ApprovalRecord resubmit(ApprovalParameter parameter) {
        checkParameter(parameter);

        return doAction(ApprovalActionType.Resubmit, parameter);
    }

    @Override
    public ApprovalRecord revoke(ApprovalParameter parameter) {
        checkParameter(parameter);

        return doAction(ApprovalActionType.Revoke, parameter);
    }

    @Override
    public ApprovalRecord check(ApprovalCheckParameter parameter) {
        checkParameter(parameter);
        Guard.notEmpty(parameter.getActionCode(), "the parameter.actionCode is required.");

        return doAction(ApprovalActionType.Check, parameter);
    }

    @Override
    public void cancel(ApprovalParameter parameter) {
        checkParameter(parameter);

        ApprovalRecord record = approvalRepository.getApprovalRecord(parameter.getRecordCode());
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", parameter.getRecordCode());
        }
        record.setStatus(ApprovalStatus.Checking);

        StateMachineFlowDefinition definition = flowFactory.loadDefinition(record);
        if(definition == null) {
            throw new ApprovalException("cannot load definition");
        }

        if(!definition.isCancelable()) {
            throw new ApprovalException("Uncanceled Definition [%s]", definition.getFlowCode());
        }

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

    protected void initFlowFactory() {
        DefaultApprovalStateMachineFlowRepository repository = 
            new DefaultApprovalStateMachineFlowRepository(approvalRepository, this::getFlowDefinition);
        flowFactory = new StateMachineFlowFactory<TApprovalQuery>(repository, null);
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

    protected StateMachineFlowDefinition getFlowDefinition(String approvalType) {
        return null;
    }

    protected ApprovalRecord doAction(ApprovalActionType actionType, ApprovalParameter parameter) {
        final String recordCode = parameter.getRecordCode();

        ApprovalRecord record = approvalRepository.getApprovalRecord(recordCode);
        if(record == null) {
            throw new ApprovalException("cannot find ApprovalRecord by recordCode[%s]", recordCode);
        }
        record.setStatus(ApprovalStatus.Checking);

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

        final String actionCode = parameter instanceof ApprovalCheckParameter cp ? cp.getActionCode() : actionType.name();
        final String itemCode = parameter instanceof IPartialItemCode pic ? pic.getItemCode() : null;

        StateMachineFlowState actionState = 
            flowEngine.action(nodeCode, actionCode, new ApprovalRecordHolder(record, historyRecord, itemCode));
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

    //#region model

    public static class ApprovalRecordHolder extends FlowActionParameter implements IPartialItemCode {

        private String itemCode;
        private ApprovalRecord record;
        private ApprovalHistoryRecord historyRecord;

        public ApprovalRecordHolder(ApprovalRecord record, ApprovalHistoryRecord historyRecord) {
            this(record, historyRecord, null);
        }

        public ApprovalRecordHolder(ApprovalRecord record, ApprovalHistoryRecord historyRecord, String itemCode) {
            this.record = record;
            this.historyRecord = historyRecord;
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

        @Override
        public String getItemCode() {
            return itemCode;
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
            
            ApprovalHistoryRecord historyRecord = null;

            if(stateParam instanceof StateMachineFlowCancelState) {
                approvalRepository.saveCancelState(record, historyRecord);
                return;
            }
            
            LinkedList<ApprovalHistoryRecord> historyRecordList = new LinkedList<>();

            StateMachineFlowState lastState = stateParam;
            while(lastState != null) {
                if(lastState instanceof StateMachinePartialState) {
                    historyRecord = new ApprovalHistoryRecord();
                    historyRecord.setHistoryRecordType(ApprovalActionType.AutoFlow);
                    historyRecord.setRemark("系统自动流转");
                } else {
                    historyRecord = recordHolder.getHistoryRecord();
                }

                historyRecord.setFlowState(stateParam);
                historyRecordList.addFirst(historyRecord);

                lastState = lastState.getPreviousFlowState();
            }

            approvalRepository.saveActionState(record, historyRecordList);
        }

        @Override
        public List<StateMachinePartialItem> getPratialItems(StateMachineFlowNode compositeNode) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    //#endregion

}
