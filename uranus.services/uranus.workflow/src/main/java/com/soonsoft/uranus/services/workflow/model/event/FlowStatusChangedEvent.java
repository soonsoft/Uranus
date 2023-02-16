package com.soonsoft.uranus.services.workflow.model.event;

import com.soonsoft.uranus.core.common.event.IEvent;
import com.soonsoft.uranus.services.workflow.model.FlowState;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public class FlowStatusChangedEvent<TFlowDefinition> implements IEvent<TFlowDefinition> {
    
    private String eventName;
    private final FlowStatus previousStatus;
    private FlowState actionFlowState;
    private final TFlowDefinition data;

    public FlowStatusChangedEvent(FlowStatus previousStatus, TFlowDefinition data) {
        this(previousStatus, null, data);
    }

    public FlowStatusChangedEvent(FlowStatus previousStatus, FlowState actionFlowState, TFlowDefinition data) {
        this.previousStatus = previousStatus;
        this.actionFlowState = actionFlowState;
        this.data = data;
    }

    public FlowStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setActionFlowState(FlowState actionFlowState) {
        this.actionFlowState = actionFlowState;
    }
    public FlowState getActionFlowState() {
        return actionFlowState;
    }

    @Override
    public TFlowDefinition getData() {
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
