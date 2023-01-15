package com.soonsoft.uranus.services.workflow.engine.linear.model;

import java.util.List;

public class LinearFlowResult extends LinearFlowState {
    
    private LinearFlowDefinition definition;
    private LinearFlowNode node;
    private List<LinearFlowNode> changedOtherNodeList;

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

    public List<LinearFlowNode> getChangedOtherNodeList() {
        return changedOtherNodeList;
    }
    public void setChangedOtherNodeList(List<LinearFlowNode> changedOtherNodeList) {
        this.changedOtherNodeList = changedOtherNodeList;
    }
}
