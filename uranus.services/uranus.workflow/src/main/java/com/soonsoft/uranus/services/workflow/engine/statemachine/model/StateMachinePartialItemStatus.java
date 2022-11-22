package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

public enum StateMachinePartialItemStatus {
    /** 待定 */
    Pending,
    /** 已处理 */
    Completed,
    /** 已取消 */
    terminated,
    ;
}
