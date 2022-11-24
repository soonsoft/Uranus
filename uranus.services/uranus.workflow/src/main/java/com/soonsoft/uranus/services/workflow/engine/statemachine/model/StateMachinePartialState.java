package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public class StateMachinePartialState extends StateMachineFlowState {
    
    private final StateMachinePartialItem actionPartialItem;
    private final StateMachineCompositeNode fromNode;

    public StateMachinePartialState(StateMachinePartialItem partialItem, StateMachineCompositeNode fromNode) {
        this.actionPartialItem = partialItem;
        this.fromNode = fromNode;
    }

    public StateMachinePartialItem getActionPartialItem() {
        return actionPartialItem;
    }

    @Override
    public void setNodeCode(String nodeCode) {
        // nodeCode is readonly
    }

    @Override
    public String getNodeCode() {
        return fromNode.getNodeCode();
    }

    @Override
    public void setStateCode(String stateCode) {
        // stateCode is readonly
    }

    @Override
    public String getStateCode() {
        return actionPartialItem.getStateCode();
    }

    @Override
    public StateMachineFlowNode getFromNode() {
        return fromNode;
    }

    @Override
    public StateMachineFlowNode getToNode() {
        return null;
    }

}
