package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import com.soonsoft.uranus.core.functional.func.Func1;

public class ParallelActionNodeState extends StateMachineFlowState {

    private StateMachinePartialItem actionPartialItem;
    private List<StateMachinePartialItem> relationPartialItems;

    public ParallelActionNodeState(Func1<String, StateMachineFlowNode> findNodeFn) {
        super(findNodeFn);
    }

    public String getActionNodeCode() {
        return actionPartialItem.getItemCode();
    }
    public StateMachineFlowNode getActionNode() {
        return getFindFlowNodeFn().call(getActionNodeCode());
    }

    public StateMachinePartialItem getActionPartialItem() {
        return actionPartialItem;
    }
    public void setActionPartialItem(StateMachinePartialItem actionPartialItem) {
        this.actionPartialItem = actionPartialItem;
    }

    public List<StateMachinePartialItem> getRelationPartialItems() {
        return relationPartialItems;
    }
    public void setRelationPartialItems(List<StateMachinePartialItem> relationPartialItems) {
        this.relationPartialItems = relationPartialItems;
    }

    @Override
    public StateMachineFlowState copy() {
        StateMachineFlowState copyState = new StateMachineFlowState();
        copy(this, copyState);
        return copyState;
    }

    public static void copy(ParallelActionNodeState source, ParallelActionNodeState dist) {
        if(source == null || dist == null) {
            return;
        }
        StateMachineFlowState.copy(source, dist);
    }
    
}
