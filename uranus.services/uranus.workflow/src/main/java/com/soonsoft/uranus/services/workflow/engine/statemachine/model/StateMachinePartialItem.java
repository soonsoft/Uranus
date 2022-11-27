package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public class StateMachinePartialItem {

    private Object id;

    private String itemCode;

    private String itemName;

    private String stateCode;

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
    
}
