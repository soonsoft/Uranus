package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class StateMachineFlowState extends FlowState {

    private String toNodeCode;

    private Func1<String, StateMachineFlowNode> findFlowNodeFn;
    
    private StateMachineFlowState previousFlowState;

    public StateMachineFlowState() {

    }

    public StateMachineFlowState(Func1<String, StateMachineFlowNode> findNodeFn) {
        this.findFlowNodeFn = findNodeFn;
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

}
