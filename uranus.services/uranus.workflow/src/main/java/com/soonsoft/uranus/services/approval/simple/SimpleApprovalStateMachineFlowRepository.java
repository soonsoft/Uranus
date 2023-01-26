package com.soonsoft.uranus.services.approval.simple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalActionType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalHistoryRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.IStateMachineFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItemStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.ParallelActionNodeState;
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
        processHistoryRecord(historyRecordList, recordHolder, stateParam);

        approvalRepository.saveActionState(record, historyRecordList);
    }

    @Override
    public List<StateMachinePartialItem> getPartialItems(StateMachineFlowNode compositeNode, Object parameter) {
        ApprovalRecord record = (ApprovalRecord) parameter;
        String approvalRecordCode = record.getRecordCode();
        String nodeCode = compositeNode.getNodeCode();
        String currentNodeMark = record.getCurrentNodeMark();

        List<ApprovalHistoryRecord> historyRecords = 
            approvalRepository.getPartialApprovalHistoryRecords(approvalRecordCode, nodeCode, currentNodeMark);

        List<StateMachinePartialItem> result = new ArrayList<>();
        if(historyRecords != null) {
            for(ApprovalHistoryRecord historyRecord : historyRecords) {
                if(historyRecord.getNodeCode().equals(nodeCode) 
                    && (!StringUtils.isEmpty(historyRecord.getCurrentNodeMark()) && historyRecord.getCurrentNodeMark().endsWith(currentNodeMark))) {
                    
                    StateMachinePartialItem partialItem = new StateMachinePartialItem();
                    partialItem.setId(historyRecord.getId());
                    partialItem.setItemCode(historyRecord.getItemCode());
                    parseToPartialItem(partialItem, historyRecord.getItemStateCode());

                    result.add(partialItem);
                }
            }
        }

        return result;
    }

    private void processHistoryRecord(
            LinkedList<ApprovalHistoryRecord> historyRecordList,
            ApprovalRecordHolder recordHolder,
            StateMachineFlowState state) {

        ApprovalRecord record = recordHolder.getRecord(); 
        ApprovalHistoryRecord historyRecord = recordHolder.getHistoryRecord();

        String pratialItemMark = record.getCurrentNodeMark();
        StateMachineFlowState previousState = state.getPreviousFlowState();
        ApprovalHistoryRecord previousHistoryRecord = null;
        while(previousState != null) {
            previousHistoryRecord = copyHistoryRecord(historyRecord);
            if(previousState.getStateCode().startsWith("@")) {
                historyRecord.setHistoryRecordType(ApprovalActionType.AutoFlow);
            }

            fillHistoryRecordState(previousHistoryRecord, previousState, pratialItemMark);
            historyRecordList.addFirst(previousHistoryRecord);

            // 添加自动取消的 PartialItem 历史记录
            List<StateMachinePartialItem> relationPartialItems = null;
            if(state instanceof CompositionPartialState partialState) {
                relationPartialItems = partialState.getRelationPartialItems();
            }
            if(state.getPreviousFlowState() instanceof ParallelActionNodeState actionNodeState) {
                relationPartialItems = actionNodeState.getRelationPartialItems();
            }
            addRelationHistoryRecord(relationPartialItems, previousHistoryRecord, pratialItemMark, historyRecordList);
            
            previousState = previousState.getPreviousFlowState();
        }

        StateMachineFlowNode nextNode = state.findToNode();
        if(nextNode != null) {
            if(nextNode instanceof StateMachineCompositeNode || nextNode instanceof StateMachineParallelNode) {
                // 如果下个节点是复合节点或并行节点，为PartialItem状态生成标记
                String partialItemMark = compositionActionCodeGenerator.call();
                record.setCurrentNodeMark(partialItemMark);
            } else {
                // 清除标记
                record.setCurrentNodeMark(null);
            }
        }

        fillHistoryRecordState(historyRecord, state, pratialItemMark);
        historyRecordList.add(historyRecord);
    }

    private ApprovalHistoryRecord copyHistoryRecord(ApprovalHistoryRecord historyRecord) {
        ApprovalHistoryRecord copy = new ApprovalHistoryRecord();
        copy.setApprovalRecordCode(historyRecord.getApprovalRecordCode());
        copy.setHistoryRecordType(historyRecord.getHistoryRecordType());
        copy.setPreviousHistoryId(historyRecord.getPreviousHistoryId());
        copy.setRemark(historyRecord.getRemark());
        copy.setOperator(historyRecord.getOperator());
        copy.setOperatorName(historyRecord.getOperatorName());
        copy.setOperateTime(historyRecord.getOperateTime());
        return copy;
    }

    private void fillHistoryRecordState(ApprovalHistoryRecord historyRecord, StateMachineFlowState state, String pratialItemMark) {
        if(state.getPreviousFlowState() instanceof CompositionPartialState partialState) {
            historyRecord.setCurrentNodeMark(pratialItemMark);
            historyRecord.setItemCode(partialState.getActionPartialItem().getItemCode());
            historyRecord.setItemStateCode(formatItemStateCode(partialState.getActionPartialItem()));
        }
        if(state.getPreviousFlowState() instanceof ParallelActionNodeState actionNodeState) {
            historyRecord.setItemCode(actionNodeState.getActionNodeCode());
            historyRecord.setItemStateCode(formatItemStateCode(actionNodeState.getActionPartialItem()));
            historyRecord.setCurrentNodeMark(pratialItemMark);
        }

        historyRecord.setNodeCode(state.getNodeCode());
        historyRecord.setStateCode(state.getStateCode());
    }

    private void addRelationHistoryRecord(
        List<StateMachinePartialItem> relationPartialItems, 
        ApprovalHistoryRecord historyRecord,
        String partialItemMark,
        LinkedList<ApprovalHistoryRecord> historyRecordList) {
        
        if(!CollectionUtils.isEmpty(relationPartialItems)) {
            for(StateMachinePartialItem item : relationPartialItems) {
                ApprovalHistoryRecord relationHistoryRecord = copyHistoryRecord(historyRecord);
                relationHistoryRecord.setNodeCode(historyRecord.getNodeCode());
                relationHistoryRecord.setStateCode(historyRecord.getStateCode());
                relationHistoryRecord.setCurrentNodeMark(partialItemMark);
                relationHistoryRecord.setItemCode(item.getItemCode());
                relationHistoryRecord.setItemStateCode(formatItemStateCode(item));
            }
        }
    }

    private String formatItemStateCode(StateMachinePartialItem partialItem) {
        return StringUtils.isEmpty(partialItem.getStateCode()) 
                    ? StringUtils.format("#{0}", partialItem.getStatus().name())
                    : StringUtils.format("{0}#{1}", partialItem.getStateCode(), partialItem.getStatus().name());
    }

    private void parseToPartialItem(StateMachinePartialItem partialItem, String itemStateCode) {
        if(StringUtils.isEmpty(itemStateCode)) {
            return;
        }

        int hashIndex = itemStateCode.indexOf("#");
        if(hashIndex > -1) {
            String stateCode = null;
            StateMachinePartialItemStatus status = null;
            if(hashIndex > 0) {
                stateCode = itemStateCode.substring(0, hashIndex);
            }
            status = StateMachinePartialItemStatus.valueOf(itemStateCode.substring(hashIndex + 1));

            partialItem.setStateCode(stateCode);
            partialItem.setStatus(status);
        } else {
            partialItem.setStateCode(itemStateCode);
        }
    }

}
