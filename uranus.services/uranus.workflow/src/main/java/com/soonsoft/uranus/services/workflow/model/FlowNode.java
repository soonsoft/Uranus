package com.soonsoft.uranus.services.workflow.model;

import java.util.List;

public class FlowNode<TState> {

    private Object id;

    private String flowCode;

    private String nodeCode;

    private String nodeName;

    private FlowNodeType nodeType;

    List<TState> stateList;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public FlowNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(FlowNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public List<TState> getStateList() {
        return stateList;
    }
    
    public void setStateList(List<TState> stateList) {
        this.stateList = stateList;
    }

    public boolean isBeginNode() {
        return this.nodeType == FlowNodeType.BeginNode;
    }

    public boolean isEndNode() {
        return this.nodeType == FlowNodeType.EndNode;
    }
    
}
