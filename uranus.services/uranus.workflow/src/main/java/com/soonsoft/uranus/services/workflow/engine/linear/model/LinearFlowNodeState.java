package com.soonsoft.uranus.services.workflow.engine.linear.model;

public class LinearFlowNodeState extends LinearFlowNode {
    
    private LinearFlowState currentState;

    public LinearFlowState getCurrentState() {
        return currentState;
    }
    public void setCurrentState(LinearFlowState currentState) {
        this.currentState = currentState;
    }

}
