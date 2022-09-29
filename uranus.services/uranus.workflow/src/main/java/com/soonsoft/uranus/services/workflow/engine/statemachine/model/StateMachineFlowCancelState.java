package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;

public class StateMachineFlowCancelState extends StateMachineFlowState {

    public StateMachineFlowCancelState(Func1<String, StateMachineFlowNode> findNodeFn) {
        super(findNodeFn);
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
    public StateMachineFlowNode getToNode() {
        return null;
    }
    
}
