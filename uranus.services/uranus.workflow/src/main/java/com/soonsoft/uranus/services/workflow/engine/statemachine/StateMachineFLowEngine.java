package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.services.workflow.IFlowDataGetter;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.ParallelActionNodeState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowBackState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
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
        StateMachineFlowNode nextNode = newState.findToNode();
        while(nextNode instanceof StateMachineGatewayNode gatewayNode) {
            // 分支节点 or 并行节点回流

            boolean isCompleted = true;
            if(nextNode instanceof StateMachineParallelNode parallelNode) {
                if(currentNode instanceof StateMachineParallelNode) {
                    // 当前是并行节点是才需处理回流
                    ParallelActionNodeState parallelActionNodeState = new ParallelActionNodeState(definition::findNode);
                    StateMachineFlowState.copy(newState, parallelActionNodeState);
                    parallelActionNodeState.setNodeCode(currentNode.getNodeCode());
                    
                    StateMachinePartialItem actionPartialItem = parallelNode.updatePartialItemState(
                        actionNode.getNodeCode(), parallelActionNodeState.getStateCode());
                    parallelActionNodeState.setActionPartialItem(actionPartialItem);
                    isCompleted = parallelNode.isCompleted();

                    newState = parallelActionNodeState;
                } else {
                    isCompleted = false;
                }
            }

            if(isCompleted) {
                // 分支节点 or 并行节点回流自动流转到下一个节点
                StateMachineFlowState nextState = gatewayNode.matchState(
                    (parameter instanceof IFlowDataGetter dataGetter) 
                        ? dataGetter.getData() 
                        : parameter);
                if(nextState == null) {
                    throw new FlowException("the gatewayNode cannot matched nextState.");
                }
                // 创建副本，避免影响原定义
                nextState = copyState(nextState, definition);
                nextState.setPreviousFlowState(newState);
                newState = nextState;
                
                nextNode = newState.findToNode();
            } else {
                // 并行节点，未全部回流完成时（此时不会更新 definition 上的状态）
                break;
            }
        }

        // 当复合节点未到达出发条件时，或者并行节点还没有完成回流时，不继续流转
        if(!(newState instanceof CompositionPartialState) && !(newState instanceof ParallelActionNodeState)) {
            updateDefinitionState(definition, newState);
            if(nextNode.isEndNode()) {
                definition.setStatus(FlowStatus.Finished);
            }
        }

        // 保存状态
        getFlowRepository().saveState(newState, parameter);

        return newState;
    }

    @Override
    public StateMachineFlowState back(String nodeCode, FlowActionParameter parameter) {
        prepareAction(nodeCode, parameter);

        final StateMachineFlowDefinition definition = getDefinition();

        StateMachineFlowNode currentNode = definition.findNode(definition.getCurrentNodeCode());
        if(currentNode == null) {
            throw new FlowException("cannot find current FlowNode by current nodeCode [%s]", definition.getCurrentNodeCode());
        }
        if(currentNode.isBeginNode()) {
            throw new FlowException("the current node [%s] is begin node, cannot back.", currentNode.getNodeCode());
        }

        StateMachineFlowNode actionNode = matchActionNode(definition, currentNode, nodeCode);
        if(actionNode == null) {
            throw new FlowException("cannot find action FlowNode by nodeCode [%s]", nodeCode);
        }

        StateMachineFlowBackState backState = definition.createBackState();
        backState.setToNodeCode(definition.getPreviousNodeCode());
        backState.setNodeCode(actionNode.getNodeCode());

        processCompositionAndParallel(currentNode, actionNode, backState, parameter);
        updateDefinitionState(definition, backState);

        // 保存状态
        getFlowRepository().saveState(backState, parameter);

        return backState;
    }

    @Override
    public void cancel(String nodeCode, FlowActionParameter parameter) {
        prepareCancel(parameter);

        final StateMachineFlowDefinition definition = getDefinition();

        StateMachineFlowNode currentNode = definition.findNode(definition.getCurrentNodeCode());
        if(currentNode == null) {
            throw new FlowException("cannot find current FlowNode by current nodeCode [%s]", definition.getCurrentNodeCode());
        }
        StateMachineFlowNode actionNode = matchActionNode(definition, currentNode, nodeCode);
        if(actionNode == null) {
            throw new FlowException("cannot find action FlowNode by nodeCode [%s]", nodeCode);
        }

        StateMachineFlowCancelState cancelState = definition.createCancelState();
        cancelState.setNodeCode(definition.getCurrentNodeCode());
        processCompositionAndParallel(currentNode, actionNode, cancelState, parameter);

        definition.setStatus(FlowStatus.Canceled);
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
        CompositionPartialState partialState = null;
        if(actionNode instanceof StateMachineCompositeNode compositeNode) {
            String partialItemCode = 
                (parameter instanceof IPartialItemCode getter) 
                    ? getter.getItemCode() 
                    : parameter.getOperator();
            StateMachinePartialItem partialItem = compositeNode.updatePartialItemState(partialItemCode, stateCode);
            partialState = new CompositionPartialState(partialItem, compositeNode);
            partialState.setFlowCode(definition.getFlowCode());

            nextStateCode = compositeNode.resolveStateCode(stateCode);
            if(!StringUtils.isEmpty(nextStateCode)) {
                // 复合节点已经得出最终状态，将剩余没有处理的节点全部终止
                List<StateMachinePartialItem> terminatedItems = compositeNode.terminateAllPendingItems();
                partialState.setRelationPartialItems(terminatedItems);
            } else {
                // 多个部分未全部完成
                return partialState;
            }
        }
        
        StateMachineFlowState state = findState(actionNode, nextStateCode);
        if(state != null) {
            StateMachineFlowState copy = copyState(state, definition);
            copy.setPreviousFlowState(partialState);
            return copy;
        }
        return null;
    }

    private void processCompositionAndParallel(StateMachineFlowNode currentNode, StateMachineFlowNode actionNode, StateMachineFlowState resolveState, FlowActionParameter parameter) {
        if(currentNode instanceof StateMachineCompositeNode compositeNode) {
            // 复合节点操作
            String partialItemCode = 
                (parameter instanceof IPartialItemCode getter) ? getter.getItemCode() : parameter.getOperator();
            StateMachinePartialItem partialItem = compositeNode.updatePartialItemState(partialItemCode, resolveState.getStateCode());
            List<StateMachinePartialItem> terminationItems = compositeNode.terminateAllPendingItems();

            CompositionPartialState partialItemState = new CompositionPartialState(partialItem, compositeNode);
            partialItemState.setFlowCode(resolveState.getFlowCode());
            partialItemState.setRelationPartialItems(terminationItems);

            // 将复合节点操作信息保留在 previousState 中，回溯是需要查阅复合节点对应的partialItems
            resolveState.setPreviousFlowState(partialItemState);
        }

        if(currentNode instanceof StateMachineParallelNode parallelNode) {
            // 并行节点操作
            ParallelActionNodeState parallelActionState = new ParallelActionNodeState(resolveState.getFindFlowNodeFn());
            StateMachineFlowState.copy(resolveState, parallelActionState);

            StateMachinePartialItem partialItem = parallelNode.updatePartialItemState(actionNode.getNodeCode(), resolveState.getStateCode());
            parallelActionState.setActionPartialItem(partialItem);

            List<StateMachinePartialItem> cancelItems = parallelNode.terminateAllPendingItems();
            parallelActionState.setRelationPartialItems(cancelItems);
            
            // 保留操作信息
            resolveState.setPreviousFlowState(parallelActionState);
        }
    }

    private void updateDefinitionState(StateMachineFlowDefinition definition, StateMachineFlowState state) {
        definition.setCurrentNodeCode(state.getToNodeCode());

        StateMachineFlowState previousState = state;
        while(previousState.getPreviousFlowState() != null) {
            if(previousState.getPreviousFlowState() instanceof CompositionPartialState) {
                break;
            }
            if(previousState.getPreviousFlowState() instanceof ParallelActionNodeState) {
                break;
            }
            previousState = previousState.getPreviousFlowState();
        }
        definition.setPreviousNodeCode(previousState.getNodeCode());
        definition.setPreviousStateCode(previousState.getStateCode());
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
        StateMachineFlowState.copy(source, copyState);
        return copyState;
    }
    
}
