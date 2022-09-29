package com.soonsoft.uranus.services.workflow.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;

public class FlowDefinition<TFlowNode extends FlowNode<?>> {

    private String flowName;

    private String flowType;

    private String flowCode;

    private String description;

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
        if(!CollectionUtils.isEmpty(nodeList)) {
            final String flowCode = getFlowCode();
            nodeList.forEach(n -> n.setFlowCode(flowCode));
        }
        this.nodeList = nodeList;
    }

    public boolean addNode(TFlowNode node) {
        if(node != null) {
            if(this.nodeList == null) {
                this.nodeList = new ArrayList<>();
            }
            node.setFlowCode(getFlowCode());
            return this.nodeList.add(node);
        }
        return false;
    }
}
