package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowCancelState extends StateMachineFlowState {

    public StateMachineFlowCancelState(StateMachineFlowDefinition definition) {
        super(definition);
    }

    @Override
    public void setStateCode(String stateCode) {
        // StateCode always be Cancel
    }

    @Override
    public String getStateCode() {
        return "Cancel";
    }

    @Override
    public void setToNodeCode(String nodeCode) {
        // No toNodeCode
    }

    @Override
    public String getToNodeCode() {
        return null;
    }

    @Override
    public FlowNode<StateMachineFlowState> getToNode() {
        return null;
    }
    
}
