package com.soonsoft.uranus.services.workflow.engine.linear;

import java.util.Arrays;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.BaseFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowStatus;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowNode;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowResult;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

/**
 * 线性流程引擎（单路径）
 */
public class LinearFlowEngine<TFlowQuery> 
                extends BaseFlowEngine<
                    LinearFlowDefinition, 
                    LinearFlowState,
                    IFlowRepository<LinearFlowDefinition, LinearFlowState>,
                    TFlowQuery
                > {

    private final int maxStep;
    private final int minStep;
    private final int[] stepArray;

    public LinearFlowEngine(LinearFlowDefinition definition) {
        this(definition, null);
    }

    public LinearFlowEngine(LinearFlowDefinition definition, TFlowQuery query) {
        super(definition, query);

        if(CollectionUtils.isEmpty(definition.getNodeList())) {
            throw new FlowException("the parameter definition.nodeList is empty.");
        }
        // 排序并去重
        definition.getNodeList().sort((a, b) -> a.getStepValue() > b.getStepValue() ? 1 : -1);
        int[] stepArr = new int[definition.getNodeList().size()];
        int current = -1;
        int index = 0;
        for(LinearFlowNode node : definition.getNodeList()) {
            if(current == -1) {
                current = node.getStepValue();
                stepArr[index] = current;
            } else {
                if(current == node.getStepValue()) {
                    continue;
                } else {
                    current = node.getStepValue();
                    index++;
                    stepArr[index] = current;
                }
            }
        }

        minStep = stepArr[0];
        maxStep = stepArr[index];
        stepArray = Arrays.copyOfRange(stepArr, 0, index + 1);
    }

    @Override
    public void start(FlowActionParameter parameter) {
        prepareStart(parameter);

        final LinearFlowDefinition definition = getDefinition();

        List<LinearFlowNode> beginNodeList = definition.findNode(minStep);
        if(CollectionUtils.isEmpty(beginNodeList)) {
            throw new FlowException("can not find start nodes.");
        }
        
        beginNodeList.forEach(n -> n.setNodeStatus(LinearFlowStatus.Activated));
        definition.setStatus(FlowStatus.Started);
        
        // 创建
        getFlowRepository().create(definition, parameter);
    }

    @Override
    public LinearFlowState action(String nodeCode, String stateCode, FlowActionParameter parameter) {
        prepareAction(nodeCode, stateCode, parameter);

        final LinearFlowDefinition definition = getDefinition();

        LinearFlowNode actionNode = null;
        for(LinearFlowNode node : definition.getNodeList()) {
            if(node.getNodeCode().equals(nodeCode)) {
                actionNode = node;
                break;
            }
        }
        if(actionNode == null) {
            throw new FlowException("cannot find activated node by nodeCode[%s]", nodeCode);
        }
        if(actionNode.getNodeStatus() != LinearFlowStatus.Activated) {
            throw new FlowException("the status of node[%s] is not activated.", nodeCode);
        }

        LinearFlowState actionState = null;
        for(LinearFlowState state : actionNode.getStateList()) {
            if(state.getStateCode().equals(stateCode)) {
                actionState = state;
                break;
            }
        }
        if(actionState == null) {
            throw new FlowException("cannot find actionState by stateCode[%s]", stateCode);
        }

        actionNode.setActionStateCode(actionState.getStateCode());
        if(actionState.getActionFn() != null) {
            // 将当前操作的 actionState映射到对应的NodeStatus
            actionState.getActionFn().apply(actionState, actionNode);
        } else {
            actionNode.setNodeStatus(LinearFlowStatus.Completed);
        }

        final int activatedStep = actionNode.getStepValue();
        List<LinearFlowNode> otherSameStepNodeList = actionNode.getSameStepNodeList();
        
        if(CollectionUtils.isEmpty(otherSameStepNodeList) 
            || !otherSameStepNodeList.stream().anyMatch(n -> n.getNodeStatus() == LinearFlowStatus.Activated)) {
            
            if(activatedStep == maxStep) {
                definition.setStatus(FlowStatus.Finished);
            } else {
                List<LinearFlowNode> nextNodeList = nextNode(activatedStep);
                if(nextNodeList == null) {
                    throw new FlowException("the activatedStep[%s] is invalid.", activatedStep);
                }
                nextNodeList.forEach(n -> n.setNodeStatus(LinearFlowStatus.Activated));
            }
        }

        LinearFlowResult result = new LinearFlowResult();
        result.setDefinition(definition);
        result.setNode(actionNode);
        result.setId(actionState.getId());
        result.setNodeCode(actionNode.getNodeCode());
        result.setStateName(actionState.getStateName());
        result.setStateCode(actionState.getStateCode());

        // 保存状态
        getFlowRepository().saveState(result, parameter);

        return result;
    }

    @Override
    public void cancel(FlowActionParameter parameter) {
        prepareCancel(parameter);

        final LinearFlowDefinition definition = getDefinition();
        definition.setStatus(FlowStatus.Canceled);

        List<LinearFlowNode> activatedNodeList = definition.findNode(n -> n.getNodeStatus() == LinearFlowStatus.Activated);
        if(!CollectionUtils.isEmpty(activatedNodeList)) {
            activatedNodeList.forEach(n -> n.setNodeStatus(LinearFlowStatus.Rejected));
        }

        LinearFlowResult result = new LinearFlowResult();
        result.setDefinition(definition);

        // 保存取消状态
        getFlowRepository().saveState(result, parameter);
    }

    @Override
    public LinearFlowState currentState() {
        throw new FlowException("the LinearFlowEngine can not support currentState() function.");
    }

    protected List<LinearFlowNode> nextNode(int currentStep) {
        if(currentStep == maxStep) {
            return null;
        }

        for(int i = 0; i < stepArray.length; i++) {
            if(stepArray[i] == currentStep) {
                return getDefinition().findNode(stepArray[i + 1]);
            }
        }
        return null;
    }

    protected int getActivatedStep() {
        LinearFlowDefinition definition = getDefinition();
        for(LinearFlowNode node : definition.getNodeList()) {
            if(node.getNodeStatus() == LinearFlowStatus.Activated) {
                return node.getStepValue();
            }
        }
        return -1;
    }
    
}
