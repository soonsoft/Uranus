package com.soonsoft.uranus.services.workflow.model.event;

import com.soonsoft.uranus.core.common.event.IEvent;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class FlowActionEvent implements IEvent<FlowState> {

    private String eventName;
    private final FlowState data;

    public FlowActionEvent(FlowState data) {
        this.data = data;
    }

    @Override
    public FlowState getData() {
        return data;
    }

    @Override
    public String getName() {
        return eventName;
    }

    public void setName(String name) {
        this.eventName = name;
    }
    
}
