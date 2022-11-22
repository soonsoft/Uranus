package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.services.workflow.IFlowDataGetter;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.behavior.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode;
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
        final StateMachineFlowNode beginNode = definition.finNode(n -> n.isBeginNode());
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

        StateMachineFlowNode newNode = newState.getToNode();
        if(newNode instanceof StateMachineGatewayNode gatewayNode) {
            // TODO：newNode is GatewayNode 需要自动处理，网关节点分为分支节点和并行节点
            boolean isCompleted = true;
            // 分支节点 or 并行节点回流
            if(gatewayNode instanceof StateMachineParallelNode parallelNode) {
                // 并行节点回流
                isCompleted = parallelNode.isCompleted();
            }

            if(isCompleted) {
                // 分支节点 or 并行节点回流自动处理下一个
                StateMachineFlowState nextState = gatewayNode.matchState(
                    (parameter instanceof IFlowDataGetter dataGetter) 
                        ? dataGetter.getData(parameter) 
                        : parameter);
                nextState.setPreviousFlowState(newState);
                newState = nextState;
            }
        }

        // 变更状态
        definition.setPreviousNodeCode(newState.getNodeCode());
        definition.setPreviousStateCode(newState.getStateCode());
        definition.setCurrentNodeCode(newNode.getNodeCode());
        if(newNode.isEndNode()) {
            definition.setStatus(FlowStatus.Finished);
        }

        // 保存状态
        // TODO: 保存 SerializedLambda 对象，通过 lambda class中的writeReplace方法得到SerializedLambda对象，通过SerializedLambda对象中的readResolve方法得到原始lambda。
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
            nextStateCode = compositeNode.resolveStateCode(stateCode, partialItemCode);
            if(nextStateCode == null) {
                // TODO：currentNode需要处理会签与或签，具体为 (stateCode, parameter) -> 返回真正的 stateCode
                // TODO： currentNode处理子流程节点
                // TODO return 中间状态
            }
        }
        
        StateMachineFlowState state = findState(actionNode, nextStateCode);
        if(state != null) {
            StateMachineFlowState newState = definition.createFlowState();
            newState.setId(state.getId());
            newState.setNodeCode(actionNode.getNodeCode());
            newState.setStateCode(state.getStateCode());
            newState.setStateName(state.getStateName());
            newState.setToNodeCode(state.getToNodeCode());
            return newState;
        }
        return null;
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
    
}
