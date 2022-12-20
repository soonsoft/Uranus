package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public class StateMachinePartialItem implements ICopy<StateMachinePartialItem> {

    /** 唯一标识 */
    private Object id;
    /** 节点参与项目编号 */
    private String itemCode;
    /** 节点参与项目名称 */
    private String itemName;
    /** 节点参与项目操作对应的状态编号（StateCode需对应到FlowNode中的States） */
    private String stateCode;
    /** 节点的状态 */
    private StateMachinePartialItemStatus status = StateMachinePartialItemStatus.Pending;

    public Object getId() {
        return id;
    }
    public void setId(Object id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStateCode() {
        return stateCode;
    }
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public StateMachinePartialItemStatus getStatus() {
        return status;
    }
    public void setStatus(StateMachinePartialItemStatus status) {
        this.status = status;
    }

    @Override
    public StateMachinePartialItem copy() {
        StateMachinePartialItem item = new StateMachinePartialItem();
        copy(this, item);
        return item;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public static void copy(StateMachinePartialItem source, StateMachinePartialItem dist) {
        if(source == null || dist == null) {
            return;
        }
        dist.setId(source.getId());
        dist.setItemCode(source.getItemCode());
        dist.setItemName(source.getItemName());
        dist.setStateCode(source.getStateCode());
    }
    
}
