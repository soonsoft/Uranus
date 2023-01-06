package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public class CompositionPartialState extends StateMachineFlowState {
    
    private final StateMachinePartialItem actionPartialItem;
    private final StateMachineCompositeNode fromNode;

    public CompositionPartialState(StateMachinePartialItem partialItem, StateMachineCompositeNode fromNode) {
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
    public StateMachineFlowNode findFromNode() {
        return fromNode;
    }

    @Override
    public StateMachineFlowNode findToNode() {
        return null;
    }

    @Override
    public CompositionPartialState copy() {
        CompositionPartialState copyPartialState = new CompositionPartialState(
            this.getActionPartialItem().copy(), (StateMachineCompositeNode) this.findFromNode().copy());
        copy(this, copyPartialState);
        return copyPartialState;
    }

    public static void copy(CompositionPartialState source, CompositionPartialState dist) {
        if(source == null || dist == null) {
            return;
        }
        StateMachineFlowState.copy(source, dist);
    }

}
