package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.services.workflow.model.FlowNode;

/**
 * 普通流程节点
 */
public class StateMachineFlowNode extends FlowNode<StateMachineFlowState> implements ICopy<StateMachineFlowNode> {

    private StateMachineFlowNodeType nodeType = StateMachineFlowNodeType.NormalNode;

    public StateMachineFlowNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(StateMachineFlowNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isBeginNode() {
        return this.nodeType == StateMachineFlowNodeType.BeginNode;
    }

    public boolean isEndNode() {
        return this.nodeType == StateMachineFlowNodeType.EndNode;
    }

    @Override
    public void setStateList(List<StateMachineFlowState> stateList) {
        if(!CollectionUtils.isEmpty(stateList)) {
            final String nodeCode = getNodeCode();
            stateList.forEach(s -> s.setNodeCode(nodeCode));
        }
        super.setStateList(stateList);
    }

    @Override
    public boolean addState(StateMachineFlowState state) {
        if(state != null) {
            state.setNodeCode(getNodeCode());
            return super.addState(state);
        }
        return false;
    }

    @Override
    public StateMachineFlowNode copy() {
        StateMachineFlowNode copyNode = new StateMachineFlowNode();
        copy(this, copyNode);
        return copyNode;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public static void copy(StateMachineFlowNode source, StateMachineFlowNode dist) {
        dist.setId(source.getId());
        dist.setFlowCode(source.getFlowCode());
        dist.setNodeCode(source.getNodeCode());
        dist.setNodeName(source.getNodeName());
        dist.setNodeType(source.getNodeType());

        if(source.getStateList() != null) {
            for(StateMachineFlowState state : source.getStateList()) {
                dist.addState(state.copy());
            }
        }
    }
}
