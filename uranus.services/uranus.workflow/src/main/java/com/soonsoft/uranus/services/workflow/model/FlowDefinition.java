package com.soonsoft.uranus.services.workflow.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;

public class FlowDefinition<TFlowNode extends FlowNode<?>> {

    /** 流程 ID */
    private Object id;
    /** 流程名称 */
    private String flowName;
    /** 流程类型 */
    private String flowType;
    /** 流程编号（唯一） */
    private String flowCode;
    /** 流程描述 */
    private String description;
    /** 流程状态 */
    private FlowStatus status = FlowStatus.Pending;
    /** 流程是否支持取消 */
    private boolean cancelable = true;
    /** 流程节点列表 */
    List<TFlowNode> nodeList;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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
