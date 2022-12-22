package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;

public class ParallelActionNodeState extends StateMachineFlowState {

    public ParallelActionNodeState(Func1<String, StateMachineFlowNode> findNodeFn) {
        super(findNodeFn);
    }

    private String actionNodeCode;

    public String getActionNodeCode() {
        return actionNodeCode;
    }

    public void setActionNodeCode(String actionNodeCode) {
        this.actionNodeCode = actionNodeCode;
    }

    public StateMachineFlowNode getActionNode() {
        return getFindFlowNodeFn().call(actionNodeCode);
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
