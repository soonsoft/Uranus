package com.soonsoft.uranus.core.common.attribute.access;

public enum ActionType {
    Add(3),
    Modify(1),
    Delete(3),
    ;

    private int priority;

    private ActionType() {
        this(1);
    }

    private ActionType(int priority) {
        this.priority = priority;
    }

    public boolean isPriority(ActionType type) {
        return type == null ? false : priority > type.priority;
    }

}
