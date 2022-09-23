package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowState {
    
    private Object id;

    private String nodeCode;

    private String stateCode;

    private String stateName;

    private String toNodeCode;

    private StateMachineFlowDefinition flowDefinition;

    private Func1<String, FlowNode<StateMachineFlowState>> findFlowNodeFn;

    public StateMachineFlowState() {

    }

    public StateMachineFlowState(StateMachineFlowDefinition definition) {
        this.flowDefinition = definition;
        this.findFlowNodeFn = nodeCode -> definition.findNode(nodeCode);
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getToNodeCode() {
        return toNodeCode;
    }

    public void setToNodeCode(String toNodeCode) {
        this.toNodeCode = toNodeCode;
    }

    public void setFindFlowNodeFn(Func1<String, FlowNode<StateMachineFlowState>> findFlowNodeFn) {
        this.findFlowNodeFn = findFlowNodeFn;
    }

    public FlowNode<StateMachineFlowState> getFromNode() {
        return findFlowNodeFn.call(getNodeCode());
    }

    public FlowNode<StateMachineFlowState> getToNode() {
        return findFlowNodeFn.call(getToNodeCode());
    }

    public StateMachineFlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

}
