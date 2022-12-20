package com.soonsoft.uranus.services.approval.simple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalPartialItem;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.IStateMachineFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItemStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;
import com.soonsoft.uranus.util.identity.ID;

public class SimpleApprovalStateMachineFlowRepository
        implements IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {
 
    private Func1<String, StateMachineFlowDefinition> findFlowDefinitionFn;
    private Func0<String> compositionActionCodeGenerator;
    private IApprovalRepository approvalRepository;

    public SimpleApprovalStateMachineFlowRepository(
            IApprovalRepository approvalRepository, 
            Func1<String, StateMachineFlowDefinition> findFlowDefinitionFn) {
        this.approvalRepository = approvalRepository;
        this.findFlowDefinitionFn = findFlowDefinitionFn;
        this.compositionActionCodeGenerator = () -> ID.newGuid();
    }

    @Override
    public StateMachineFlowDefinition getDefinition(String flowCode) {
        // 这里的flowCode就是approvalType
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
        // 因为submit时会将Flow Engine的start与第一步action合并，所以start对应的create接口不做处理
    }

    @Override
    public void saveState(StateMachineFlowState stateParam, FlowActionParameter parameter) {
        ApprovalRecordHolder recordHolder = (ApprovalRecordHolder) parameter;

        ApprovalRecord record = recordHolder.getRecord();
        record.setFlowState(stateParam);

        if(ApprovalStateCode.Checking.equals(stateParam.getStateCode())) {
            record.setStatus(ApprovalStatus.Checking);
            approvalRepository.create(record, recordHolder.getHistoryRecord());
            return;
        }

        if(stateParam instanceof StateMachineFlowCancelState) {
            record.setStatus(ApprovalStatus.Canceled);
            approvalRepository.saveCancelState(record, recordHolder.getHistoryRecord());
            return;
        }

        StateMachineFlowDefinition currentDefinition = recordHolder.getCurrentDefinition();
        if(currentDefinition.getStatus() == FlowStatus.Finished) {
            record.setStatus(ApprovalStatus.Completed);
        }
        
        LinkedList<ApprovalHistoryRecord> historyRecordList = new LinkedList<>();
        List<ApprovalPartialItem> partialItems = new ArrayList<>();
        processHistoryRecord(historyRecordList, partialItems, recordHolder, stateParam);

        approvalRepository.saveActionState(record, historyRecordList, partialItems);
    }

    @Override
    public List<StateMachinePartialItem> getPartialItems(StateMachineFlowNode compositeNode) {
        return approvalRepository.getPartialItems(compositeNode);
    }

    private void processHistoryRecord(
            LinkedList<ApprovalHistoryRecord> historyRecordList,
            List<ApprovalPartialItem> partialItems,
            ApprovalRecordHolder recordHolder,
            StateMachineFlowState state) {

        ApprovalRecord record = recordHolder.getRecord(); 
        ApprovalHistoryRecord historyRecord = recordHolder.getHistoryRecord();

        StateMachineFlowNode node = state.getFromNode();
        if(node instanceof StateMachineCompositeNode compositeNode) {
            if(state instanceof CompositionPartialState partialState) {
                partialItems.add(creataApprovalPartialItem(partialState.getActionPartialItem(), record.getApprovalFlowCode()));
            } else {
                compositeNode.forEach((item, i, b) -> {
                    partialItems.add(creataApprovalPartialItem(item, record.getApprovalFlowCode()));
                });
            }
            return;
        }

        StateMachineFlowNode nextNode = state.getToNode();
        if(nextNode instanceof StateMachineCompositeNode compositeNode) {
            String compositionActionCode = compositionActionCodeGenerator.call();
            record.setCompositionActionCode(compositionActionCode);
            historyRecord.setCompositionActionCode(compositionActionCode);
            historyRecordList.add(historyRecord);

            compositeNode.forEach((item, i, b) -> {
                partialItems.add(creataApprovalPartialItem(item, compositionActionCode));
            });
            return;
        }

        record.setCompositionActionCode(null);
        if(node instanceof StateMachineGatewayNode gatewayNode) {
            StateMachineFlowState lastState = state;
            while(lastState != null) {
                if(lastState instanceof CompositionPartialState partialState) {
                    historyRecord = new ApprovalHistoryRecord();
                    historyRecord.setHistoryRecordType(ApprovalActionType.AutoFlow);
                    historyRecord.setApprovalRecordCode(record.getRecordCode());
                    historyRecord.setOperateTime(recordHolder.getOperateTime());
                } else {
                    historyRecord = recordHolder.getHistoryRecord();
                }
                historyRecord.setFlowState(lastState);
                historyRecordList.addFirst(historyRecord);

                lastState = lastState.getPreviousFlowState();
            }
            return;
        }

        historyRecordList.add(historyRecord);
    }

    private ApprovalPartialItem creataApprovalPartialItem(StateMachinePartialItem item, String compositionActionCode) {
        ApprovalPartialItem partialItem = new ApprovalPartialItem();
        StateMachinePartialItem.copy(item, partialItem);
        partialItem.setCompositionActionCode(compositionActionCode);
        partialItem.setStatus(StateMachinePartialItemStatus.Pending);
        return partialItem;
    }

}
