package com.soonsoft.uranus.services.workflow.engine.linear.model;

public class LinearFlowResult extends LinearFlowState {
    
    private LinearFlowDefinition definition;
    private LinearFlowNode node;

    public LinearFlowDefinition getDefinition() {
        return definition;
    }
    public void setDefinition(LinearFlowDefinition definition) {
        this.definition = definition;
    }
    public LinearFlowNode getNode() {
        return node;
    }
    public void setNode(LinearFlowNode node) {
        this.node = node;
    }

    
}
