package com.soonsoft.uranus.services.workflow.engine.linear.model;

import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.services.workflow.model.FlowNode;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public class LinearFlowState extends FlowState {

    private Action2<LinearFlowState, FlowNode<LinearFlowState>> actionFn;

    public Action2<LinearFlowState, FlowNode<LinearFlowState>> getActionFn() {
        return actionFn;
    }

    public void setActionFn(Action2<LinearFlowState, FlowNode<LinearFlowState>> actionFn) {
        this.actionFn = actionFn;
    }
    
}
