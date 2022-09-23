package com.soonsoft.uranus.services.workflow.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;

public class FlowDefinition<TFlowNode extends FlowNode<?>> {

    private String flowName;

    private String flowType;

    private String flowCode;

    private String description;

    private String currentNodeCode;

    private String previousStateCode;

    private String previousNodeCode;

    private FlowStatus status = FlowStatus.Pending;

    private boolean cancelable = true;

    List<TFlowNode> nodeList;

    public StateMachineFlowState getCurrentState() {
        return null;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrentNodeCode() {
        return currentNodeCode;
    }

    public void setCurrentNodeCode(String currentNodeCode) {
        this.currentNodeCode = currentNodeCode;
    }

    public String getPreviousNodeCode() {
        return previousNodeCode;
    }

    public void setPreviousNodeCode(String previousNodeCode) {
        this.previousNodeCode = previousNodeCode;
    }

    public String getPreviousStateCode() {
        return previousStateCode;
    }

    public void setPreviousStateCode(String previousStateCode) {
        this.previousStateCode = previousStateCode;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public List<TFlowNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TFlowNode> nodeList) {
        this.nodeList = nodeList;
    }

    public void addNode(TFlowNode node) {
        if(this.nodeList == null) {
            this.nodeList = new ArrayList<>();
        }
        this.nodeList.add(node);
    }
}
