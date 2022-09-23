package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowNode;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

/**
 * 状态机流程引擎（多路径）
 */
public class StateMachineFLowEngine<TFlowQuery> 
                extends BaseFlowEngine<StateMachineFlowDefinition, IFlowRepository<StateMachineFlowState>, TFlowQuery> {

    public StateMachineFLowEngine(StateMachineFlowDefinition definition, TFlowQuery query) {
        super(definition, query);
    }

    @Override
    public void start(FlowActionParameter parameter) {
        if(getStatus() != FlowStatus.Pending) {
            throw new FlowException(
                "start flow process error, cause the status of flow process is incurrect, current status is [%s]", 
                getStatus().name());
        }
        final StateMachineFlowDefinition definition = this.getDefinition();
        final FlowNode<StateMachineFlowState> beginNode = definition.finNode(n -> n.isBeginNode());
        if(beginNode == null) {
            throw new FlowException("cannot find begin flow node in nodelist.");
        }
        definition.setCurrentNodeCode(beginNode.getNodeCode());
        definition.setStatus(FlowStatus.Started);

        // 创建工作流数据
        getFlowRepository().create(definition, parameter);
    }

    public StateMachineFlowState action(String stateCode) {
        final StateMachineFlowDefinition definition = getDefinition();
        return action(definition.getCurrentNodeCode(), stateCode);
    }

    @Override
    public StateMachineFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter) {
        if(!isStarted()) {
            throw new FlowException(
                "the flow process do action error, cause the status is incurrect, current status is [%s]", 
                getStatus().name());
        }

        Guard.notEmpty(nodeCode, "the parameter nodeCode is required.");
        Guard.notEmpty(stateCode, "the parameter stateCode is required.");

        final StateMachineFlowDefinition definition = getDefinition();

        if(definition.getCurrentNodeCode() != null && !definition.getCurrentNodeCode().equals(nodeCode)) {
            throw new FlowException(
                "the current node code of definition is [%s], but the parameter nodeCode is [%s]", 
                definition.getCurrentNodeCode(), nodeCode);
        }

        FlowNode<StateMachineFlowState> currentNode = definition.findNode(nodeCode);
        if(currentNode == null) {
            throw new FlowException("can not find FlowNode by nodeCode [%s]", nodeCode);
        }

        StateMachineFlowState newState = null;
        StateMachineFlowState state = findState(currentNode, stateCode);
        if(state != null) {
            newState = definition.createFlowState();
            newState.setId(state.getId());
            newState.setNodeCode(currentNode.getNodeCode());
            newState.setStateCode(state.getStateCode());
            newState.setStateName(state.getStateName());
            newState.setToNodeCode(state.getToNodeCode());
        }

        if(newState == null) {
            throw new FlowException("the stateCode[%s] cannot be matched in current flow node[%s]", stateCode, nodeCode);
        }

        FlowNode<StateMachineFlowState> newNode = newState.getToNode();
        // 变更状态
        definition.setPreviousNodeCode(newState.getNodeCode());
        definition.setPreviousStateCode(newState.getStateCode());
        definition.setCurrentNodeCode(newNode.getNodeCode());
        if(newNode.isEndNode()) {
            definition.setStatus(FlowStatus.Finished);
        }

        // 保存状态
        getFlowRepository().saveState(newState, parameter);

        return newState;
    }

    @Override
    public void cancel(FlowActionParameter parameter) {
        final StateMachineFlowDefinition definition = getDefinition();
        if(!definition.isCancelable()) {
            throw new FlowException("the flow definition [%s] is not supported", definition.getFlowName());
        }

        FlowStatus status = getStatus();
        if(status == FlowStatus.Canceled || status == FlowStatus.Finished) {
            throw new FlowException("the flow process is done, current status is [%s]", status.name());
        }
        
        definition.setStatus(FlowStatus.Canceled);

        StateMachineFlowCancelState cancelState = definition.createCancelState();
        cancelState.setNodeCode(definition.getCurrentNodeCode());

        // 保存取消状态
        getFlowRepository().saveState(cancelState, parameter);
    }

    @Override
    public StateMachineFlowState currentState() {
        final StateMachineFlowDefinition definition = getDefinition();
        FlowNode<StateMachineFlowState> previousFlowNode = definition.findNode(definition.getPreviousNodeCode());
        if(previousFlowNode == null) {
            return null;
        }

        StateMachineFlowState currentState = findState(previousFlowNode, definition.getPreviousStateCode());
        return currentState;
    }

    private StateMachineFlowState findState(FlowNode<StateMachineFlowState> node, String stateCode) {
        List<StateMachineFlowState> stateList = node.getStateList();
        for(StateMachineFlowState state : stateList) {
            if(state.getStateCode().equals(stateCode)) {
                return state;
            }
        }
        return null;
    }
    
}
