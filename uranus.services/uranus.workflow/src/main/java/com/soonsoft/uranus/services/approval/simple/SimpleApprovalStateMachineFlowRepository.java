package com.soonsoft.uranus.services.approval.simple;

import java.util.LinkedList;
import java.util.List;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.workflow.engine.statemachine.IStateMachineFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class SimpleApprovalStateMachineFlowRepository
        implements IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {
 
    private Func1<String, StateMachineFlowDefinition> findFlowDefinitionFn;
    private IApprovalRepository approvalRepository;

    public SimpleApprovalStateMachineFlowRepository(
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
