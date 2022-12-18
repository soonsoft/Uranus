package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.services.workflow.IFlowDataGetter;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelNode;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

/**
 * 状态机流程引擎（多路径）
 */
public class StateMachineFLowEngine<TFlowQuery> 
                extends BaseFlowEngine<
                    StateMachineFlowDefinition, 
                    StateMachineFlowState, 
                    IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState>, 
                    TFlowQuery
                > {

    public StateMachineFLowEngine(StateMachineFlowDefinition definition, TFlowQuery query) {
        super(definition, query);
    }

    @Override
    public void start(FlowActionParameter parameter) {
        prepareStart(parameter);

        final StateMachineFlowDefinition definition = this.getDefinition();
        final StateMachineFlowNode beginNode = definition.findNode(n -> n.isBeginNode());
        if(beginNode == null) {
            throw new FlowException("cannot find begin flow node in nodelist.");
        }
        definition.setPreviousNodeCode(null);
        definition.setCurrentNodeCode(beginNode.getNodeCode());
        definition.setStatus(FlowStatus.Started);

        // 创建工作流数据
        getFlowRepository().create(definition, parameter);
    }

    @Override
    public StateMachineFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter) {
        prepareAction(nodeCode, stateCode, parameter);

        final StateMachineFlowDefinition definition = getDefinition();

        if(StringUtils.isEmpty(definition.getCurrentNodeCode())) {
            throw new FlowException("the current node code of definition is null.");
        }

        StateMachineFlowNode currentNode = definition.findNode(definition.getCurrentNodeCode());
        if(currentNode == null) {
            throw new FlowException("can not find current FlowNode by current nodeCode [%s]", definition.getCurrentNodeCode());
        }

        StateMachineFlowNode actionNode = matchActionNode(definition, currentNode, nodeCode);
        if(actionNode == null) {
            throw new FlowException("can not find action FlowNode by nodeCode [%s]", nodeCode);
        }

        StateMachineFlowState newState = matchNewState(definition, actionNode, stateCode, parameter);
        if(newState == null) {
            throw new FlowException("the stateCode[%s] cannot be matched in current flow node[%s]", stateCode, nodeCode);
        }

        // 自动处理所有的 GatewayNode（GatewayNode 分为分支节点和并行节点，不能作为结束节点）
        StateMachineFlowNode newNode = newState.getToNode();
        while(newNode instanceof StateMachineGatewayNode gatewayNode) {
            boolean isCompleted = true;
            // 分支节点 or 并行节点回流
            if(gatewayNode instanceof StateMachineParallelNode parallelNode) {
                // 并行节点状态回流
                parallelNode.updatePartialItemState(newState.getNodeCode(), newState.getStateCode());
                isCompleted = parallelNode.isCompleted();
            }

            if(isCompleted) {
                // 分支节点 or 并行节点回流自动处理下一个
                StateMachineFlowState nextState = gatewayNode.matchState(
                    (parameter instanceof IFlowDataGetter dataGetter) 
                        ? dataGetter.getData() 
                        : parameter);
                nextState = copyState(nextState, definition);
                newNode = newState.getToNode();
                nextState.setPreviousFlowState(newState);
                newState = nextState;
            } else {
                // 并行回流节点，未回流完成时跳出
                break;
            }
        }

        // 当复合节点未到达出发条件时，或者并行节点还没有完成回流时，不继续流转
        if(!(newState instanceof StateMachinePartialState) && newNode != currentNode) {
            updateDefinitionState(definition, newState);
            if(newNode.isEndNode()) {
                definition.setStatus(FlowStatus.Finished);
            }
        }

        // 保存状态
        getFlowRepository().saveState(newState, parameter);

        return newState;
    }

    @Override
    public void cancel(FlowActionParameter parameter) {
        prepareCancel(parameter);

        final StateMachineFlowDefinition definition = getDefinition();
        definition.setStatus(FlowStatus.Canceled);

        StateMachineFlowCancelState cancelState = definition.createCancelState();
        cancelState.setNodeCode(definition.getCurrentNodeCode());

        updateDefinitionState(definition, cancelState);

        // 保存取消状态
        getFlowRepository().saveState(cancelState, parameter);
    }

    @Override
    public StateMachineFlowState currentState() {
        final StateMachineFlowDefinition definition = getDefinition();
        StateMachineFlowNode previousFlowNode = definition.findNode(definition.getPreviousNodeCode());
        if(previousFlowNode == null) {
            return null;
        }

        StateMachineFlowState currentState = findState(previousFlowNode, definition.getPreviousStateCode());
        return currentState;
    }

    protected StateMachineFlowNode matchActionNode(
            StateMachineFlowDefinition definition, StateMachineFlowNode currentNode, String nodeCode) {
        if(currentNode instanceof StateMachineParallelNode parallelNode) {
            return parallelNode.getActionNode(nodeCode);
        } else {
            if(!currentNode.getNodeCode().equals(nodeCode)) {
                throw new FlowException(
                    "the current node code of definition is [%s], but the parameter nodeCode is [%s]", 
                    definition.getCurrentNodeCode(), nodeCode);
            }
            return currentNode;
        }
    }

    protected StateMachineFlowState matchNewState(
            StateMachineFlowDefinition definition, 
            StateMachineFlowNode actionNode, 
            String stateCode, 
            FlowActionParameter parameter) {
        
        String nextStateCode = stateCode;
        if(actionNode instanceof StateMachineCompositeNode compositeNode) {
            String partialItemCode = 
                (parameter instanceof IPartialItemCode getter) 
                    ? getter.getItemCode() 
                    : parameter.getOperator();
            StateMachinePartialItem partialItem = compositeNode.updatePartialItemState(partialItemCode, stateCode);
            nextStateCode = compositeNode.resolveStateCode(stateCode);
            if(StringUtils.isEmpty(nextStateCode)) {
                // 多个部分未全部完成
                StateMachinePartialState partialState = new StateMachinePartialState(partialItem, compositeNode);
                partialState.setFlowCode(definition.getFlowCode());
                return partialState;
            }
        }
        
        StateMachineFlowState state = findState(actionNode, nextStateCode);
        if(state != null) {
            return copyState(state, definition);
        }
        return null;
    }

    private void updateDefinitionState(StateMachineFlowDefinition definition, StateMachineFlowState state) {
        definition.setPreviousNodeCode(state.getNodeCode());
        definition.setPreviousStateCode(state.getStateCode());
        definition.setCurrentNodeCode(state.getToNodeCode());
    }

    private StateMachineFlowState findState(StateMachineFlowNode node, String stateCode) {
        List<StateMachineFlowState> stateList = node.getStateList();
        for(StateMachineFlowState state : stateList) {
            if(state.getStateCode().equals(stateCode)) {
                return state;
            }
        }
        return null;
    }

    private StateMachineFlowState copyState(StateMachineFlowState source, StateMachineFlowDefinition definition) {
        StateMachineFlowState copyState = definition.createFlowState();
        copyState.setId(source.getId());
        copyState.setNodeCode(source.getNodeCode());
        copyState.setStateCode(source.getStateCode());
        copyState.setStateName(source.getStateName());
        copyState.setToNodeCode(source.getToNodeCode());
        return copyState;
    }
    
}
