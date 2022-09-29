package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class StateMachineFlowState extends FlowState {

    private String toNodeCode;

    private StateMachineFlowDefinition flowDefinition;

    private Func1<String, StateMachineFlowNode> findFlowNodeFn;

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

    public void setFindFlowNodeFn(Func1<String, StateMachineFlowNode> findFlowNodeFn) {
        this.findFlowNodeFn = findFlowNodeFn;
    }

    public StateMachineFlowNode getFromNode() {
        return findFlowNodeFn.call(getNodeCode());
    }

    public StateMachineFlowNode getToNode() {
        return findFlowNodeFn.call(getToNodeCode());
    }

    public StateMachineFlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

}
