package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.beans.Transient;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class StateMachineFlowState extends FlowState implements ICopy<StateMachineFlowState> {

    /** 所属节点ID（扩展字段） */
    private Object fromNodeId;
    /** 流转到达节点ID（扩展字段） */
    private Object toNodeId;
    /** 流转到达节点编码 */
    private String toNodeCode;
    /** 获取节点信息函数 */
    private transient Func1<String, StateMachineFlowNode> findFlowNodeFn;
    /** 上一个操作节点（自动流转时填充，如GatewayNode） */
    private transient StateMachineFlowState previousFlowState;

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
    @Transient
    protected Func1<String, StateMachineFlowNode> getFindFlowNodeFn() {
        return findFlowNodeFn;
    }

    @Transient
    public StateMachineFlowState getPreviousFlowState() {
        return previousFlowState;
    }
    public void setPreviousFlowState(StateMachineFlowState previousFlowState) {
        this.previousFlowState = previousFlowState;
    }

    public StateMachineFlowNode findFromNode() {
        return findFlowNodeFn.call(getNodeCode());
    }

    public StateMachineFlowNode findToNode() {
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

    public static void copy(StateMachineFlowState source, StateMachineFlowState dist) {
        if(source == null || dist == null) {
            return;
        }
        dist.setId(source.getId());
        dist.setFlowCode(source.getFlowCode());
        dist.setFromNodeId(source.getFromNodeId());
        dist.setNodeCode(source.getNodeCode());
        dist.setStateCode(source.getStateCode());
        dist.setStateName(source.getStateName());
        dist.setToNodeCode(source.getToNodeCode());
        dist.setToNodeId(source.getToNodeId());
    }

}
