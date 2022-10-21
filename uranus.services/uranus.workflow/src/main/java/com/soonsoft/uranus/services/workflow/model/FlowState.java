package com.soonsoft.uranus.services.workflow.model;

public class FlowState {

    /** 流程节点状态 ID */
    private Object id;
    /** 所属流程节点编号 */
    private String nodeCode;
    /** 流程节点状态编号 */
    private String stateCode;
    /** 流程节点状态名称 */
    private String stateName;

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
    
}
