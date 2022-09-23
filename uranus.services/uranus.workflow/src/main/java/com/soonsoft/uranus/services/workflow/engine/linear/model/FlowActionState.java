package com.soonsoft.uranus.services.workflow.engine.linear.model;

import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class FlowActionState {

    private String stateValue;

    private Action2<String, FlowNode<FlowActionState>> actionFn;
    
}
