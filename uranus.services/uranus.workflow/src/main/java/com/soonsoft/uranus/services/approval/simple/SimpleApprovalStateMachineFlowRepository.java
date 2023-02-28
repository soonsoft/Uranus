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
        return findFlowDefinitionFn != null ? findFlowDefinitionFn.call(flowCode) : null;
    }

    @Override
    public StateMachineFlowState getCurrentState(Object parameter) {
        String recordCode = null;
        ApprovalRecord record = null;
        if(parameter instanceof ApprovalRecord) {
            record = (ApprovalRecord) parameter;
        } else if(parameter instanceof ApprovalCheckParameter p) {
            recordCode = p.getRecordCode();
        } else if(parameter instanceof String code) {
            recordCode = code;
        } else {
            throw new ApprovalException("unknown parameter type.");
        }

        if(!StringUtils.isEmpty(recordCode)) {
            record = approvalRepository.getApprovalRecord(recordCode);
        }

        if(record != null) {
            StateMachineFlowState state = new StateMachineFlowState();
            state.setFlowCode(record.getApprovalFlowCode());
            state.setNodeCode(record.getPreviousNodeCode());
            state.setStateCode(record.getPreviousStateCode());
            state.setToNodeCode(record.getCurrentNodeCode());
            return state;
        }
        
        return null;
    }

    @Override
    public void create(StateMachineFlowDefinition definition, FlowActionParameter parameter) {
        // 因为submit时会将Flow Engine的start与第一步action合并，所以start对应的create接口不做处理
    }

    @Override
    public void saveState(StateMachineFlowState stateParam, FlowActionParameter parameter) {
        ApprovalRecordHolder recordHolder = (ApprovalRecordHolder) parameter;

        ApprovalRecord record = recordHolder.getRecord();
        // 设置状态
        StateMachineFlowDefinition definition = recordHolder.getCurrentDefinition();
        record.setPreviousNodeCode(definition.getPreviousNodeCode());
        record.setPreviousStateCode(definition.getPreviousStateCode());
        record.setCurrentNodeCode(definition.getCurrentNodeCode());

        ApprovalHistoryRecord historyRecord = recordHolder.getHistoryRecord();
        // 设置当前操作记录的状态数据
        fillHistoryRecordState(historyRecord, stateParam, record.getCurrentNodeMark());
        record.setCurrentHistoryRecord(historyRecord);

        if(ApprovalStateCode.Checking.equals(stateParam.getStateCode())) {
            record.setStatus(ApprovalStatus.Checking);
            approvalRepository.create(record, historyRecord);
            return;
        }

        if(stateParam instanceof StateMachineFlowCancelState) {
            record.setStatus(ApprovalStatus.Canceled);
            approvalRepository.saveCancelState(record, historyRecord);
            return;
        }

        StateMachineFlowDefinition currentDefinition = recordHolder.getCurrentDefinition();
        if(currentDefinition.getStatus() == FlowStatus.Finished) {
            record.setStatus(ApprovalStatus.Completed);
        }
        
        LinkedList<ApprovalHistoryRecord> historyRecordList = new LinkedList<>();
        processHistoryRecord(historyRecordList, recordHolder, stateParam);

        approvalRepository.saveActionState(record, historyRecordList);
        // 保存完成后，将操作记录ID更新到审核记录中
        record.setCurrentHistoryId(historyRecord.getId());
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

        final String pratialItemMark = record.getCurrentNodeMark();
        StateMachineFlowState previousState = state;
        ApprovalHistoryRecord previousHistoryRecord = null;
        while(previousState != null) {
            // 添加自动取消的 PartialItem 历史记录
            List<StateMachinePartialItem> relationPartialItems = null;
            if(previousState instanceof CompositionPartialState partialState) {
                relationPartialItems = partialState.getRelationPartialItems();
            }
            if(previousState instanceof ParallelActionNodeState actionNodeState) {
                relationPartialItems = actionNodeState.getRelationPartialItems();
            }
            addRelationHistoryRecord(relationPartialItems, previousHistoryRecord, pratialItemMark, historyRecordList);
            
            // 添加进行操作的 PartialItem 历史记录（操作的PartialItem历史记录在前，关联操作的历史记录在后）
            if(previousState == state) {
                previousHistoryRecord = historyRecord;
            } else {
                previousHistoryRecord = copyHistoryRecord(historyRecord);
                fillHistoryRecordState(previousHistoryRecord, previousState, pratialItemMark);
            }
            if(previousState.getStateCode().startsWith("@")) {
                historyRecord.setHistoryRecordType(ApprovalActionType.AutoFlow);
            }
            historyRecordList.addFirst(previousHistoryRecord);

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
        historyRecord.setNodeCode(state.getNodeCode());
        historyRecord.setStateCode(state.getStateCode());
        historyRecord.setToNodeCode(state.getToNodeCode());
        
        if(state instanceof CompositionPartialState partialState) {
            historyRecord.setCurrentNodeMark(pratialItemMark);
            historyRecord.setItemCode(partialState.getActionPartialItem().getItemCode());
            historyRecord.setItemStateCode(formatItemStateCode(partialState.getActionPartialItem()));
        }
        if(state instanceof ParallelActionNodeState actionNodeState) {
            historyRecord.setCurrentNodeMark(pratialItemMark);
            historyRecord.setItemCode(actionNodeState.getActionNodeCode());
            historyRecord.setItemStateCode(formatItemStateCode(actionNodeState.getActionPartialItem()));
        }
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
                // 操作关联产生的其他部分项目无需设置到达节点
                relationHistoryRecord.setToNodeCode(null);
                relationHistoryRecord.setCurrentNodeMark(partialItemMark);
                relationHistoryRecord.setItemCode(item.getItemCode());
                relationHistoryRecord.setItemStateCode(formatItemStateCode(item));

                historyRecordList.addFirst(relationHistoryRecord);
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
