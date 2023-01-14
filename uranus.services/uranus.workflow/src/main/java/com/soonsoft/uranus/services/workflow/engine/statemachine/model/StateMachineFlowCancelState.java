package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;

public class StateMachineFlowCancelState extends StateMachineFlowState {

    private static final String CANCEL_STATE_CODE = "@Cancel";

    public StateMachineFlowCancelState(Func1<String, StateMachineFlowNode> findNodeFn) {
        super(findNodeFn);
    }

    @Override
    public void setStateCode(String stateCode) {
        // StateCode be always @Cancel
    }
    @Override
    public String getStateCode() {
        return CANCEL_STATE_CODE;
    }

    @Override
    public void setToNodeId(Object toNodeId) {
        // no toNodeId
    }
    @Override
    public Object getToNodeId() {
        return null;
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
    public StateMachineFlowNode findToNode() {
        return null;
    }

    public static boolean isCancelState(String stateCode) {
        return CANCEL_STATE_CODE.equals(stateCode);
    }

    @Override
    public StateMachineFlowCancelState copy() {
        StateMachineFlowCancelState copyCancelState = new StateMachineFlowCancelState(this.getFindFlowNodeFn());
        copy(this, copyCancelState);
        return copyCancelState;
    }

    public static void copy(StateMachineFlowCancelState source, StateMachineFlowCancelState dist) {
        if(source == null || dist == null) {
            return;
        }
        dist.setId(source.getId());
        dist.setFlowCode(source.getFlowCode());
        dist.setFromNodeId(source.getFromNodeId());
        dist.setNodeCode(source.getNodeCode());
        dist.setStateName(source.getStateName());
    }
    
}
