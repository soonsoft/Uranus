package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;

public class StateMachineFlowBackState extends StateMachineFlowState {

    private static final String BACK_STATE_CODE = "@Back";

    public StateMachineFlowBackState(Func1<String, StateMachineFlowNode> findNodeFn) {
        super(findNodeFn);
    }

    @Override
    public void setStateCode(String stateCode) {
        // StateCode be always @Back
    }
    @Override
    public String getStateCode() {
        return BACK_STATE_CODE;
    }

    public static boolean isBackState(String stateCode) {
        return BACK_STATE_CODE.equals(stateCode);
    }

    @Override
    public StateMachineFlowBackState copy() {
        StateMachineFlowBackState copyCancelState = new StateMachineFlowBackState(this.getFindFlowNodeFn());
        copy(this, copyCancelState);
        return copyCancelState;
    }

    public static void copy(StateMachineFlowBackState source, StateMachineFlowBackState dist) {
        if(source == null || dist == null) {
            return;
        }
        StateMachineFlowState.copy(source, dist);
    }
    
}
