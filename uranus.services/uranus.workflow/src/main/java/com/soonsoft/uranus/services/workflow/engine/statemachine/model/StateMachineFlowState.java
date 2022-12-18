package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class StateMachineFlowState extends FlowState implements ICopy<StateMachineFlowState> {

    private Object fromNodeId;
    private Object toNodeId;
    private String toNodeCode;

    private Func1<String, StateMachineFlowNode> findFlowNodeFn;
    
    private StateMachineFlowState previousFlowState;

    public StateMachineFlowState() {

    }

    public StateMachineFlowState(Func1<String, StateMachineFlowNode> findNodeFn) {
        this.findFlowNodeFn = findNodeFn;
    }
    
    public Object getFromNodeId() {
        return fromNodeId;
    }
    public void setFromNodeId(Object fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public Object getToNodeId() {
        return toNodeId;
    }
    public void setToNodeId(Object toNodeId) {
        this.toNodeId = toNodeId;
    }

    public String getToNodeCode() {
        return toNodeCode;
    }
    public void setToNodeCode(String toNodeCode) {
        this.toNodeCode = toNodeCode;
    }

    public void setFindFlowNodeFn(Func1<String, StateMachineFlowNode> findNodeFn) {
        this.findFlowNodeFn = findNodeFn;
    }

    public StateMachineFlowState getPreviousFlowState() {
        return previousFlowState;
    }
    public void setPreviousFlowState(StateMachineFlowState previousFlowState) {
        this.previousFlowState = previousFlowState;
    }

    public StateMachineFlowNode getFromNode() {
        return findFlowNodeFn.call(getNodeCode());
    }

    public StateMachineFlowNode getToNode() {
        return findFlowNodeFn.call(getToNodeCode());
    }

    @Override
    public StateMachineFlowState copy() {
        StateMachineFlowState state = new StateMachineFlowState();
        copy(this, state);
        return state;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public static void copy(StateMachineFlowState source, StateMachineFlowState dict) {
        dict.setId(source.getId());
        dict.setFlowCode(source.getFlowCode());
        dict.setFromNodeId(source.getFromNodeId());
        dict.setNodeCode(source.getNodeCode());
        dict.setStateCode(source.getStateCode());
        dict.setToNodeCode(source.getToNodeCode());
        dict.setToNodeId(source.getToNodeId());
    }

}
