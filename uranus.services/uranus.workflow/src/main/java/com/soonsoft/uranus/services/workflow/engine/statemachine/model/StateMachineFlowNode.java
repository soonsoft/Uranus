package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowNode extends FlowNode<StateMachineFlowState> {

    private StateMachineFlowNodeType nodeType;

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
            if(getStateList() == null) {
                setStateList(new ArrayList<>());
            }
            state.setNodeCode(getNodeCode());
            return super.addState(state);
        }
        return false;
    }
}
