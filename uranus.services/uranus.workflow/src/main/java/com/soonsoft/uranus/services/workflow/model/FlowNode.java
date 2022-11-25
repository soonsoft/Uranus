package com.soonsoft.uranus.services.workflow.model;

import java.util.ArrayList;
import java.util.List;

public class FlowNode<TState extends FlowState> {

    /** 流程节点 ID */
    private Object id;
    /** 所属流程编号 */
    private String flowCode;
    /** 流程节点编号 */
    private String nodeCode;
    /** 流程节点名称 */
    private String nodeName;
    /** 流程节点（可产生）状态列表 */
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

    public List<TState> getStateList() {
        return stateList;
    }
    
    public void setStateList(List<TState> stateList) {
        this.stateList = stateList;
    }

    public boolean addState(TState state) {
        if(state != null) {
            if(stateList == null) {
                stateList = new ArrayList<>();
            }
            return stateList.add(state);
        }
        return false;
    }
    
}
