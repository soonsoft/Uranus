package com.soonsoft.uranus.services.workflow.engine.linear;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class LinearFlowRepository implements IFlowRepository<LinearFlowDefinition, LinearFlowState> {

    @Override
    public LinearFlowDefinition getDefinition(Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FlowNode<?> getCurrentNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LinearFlowState getCurrentState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(LinearFlowDefinition definition, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveState(LinearFlowState stateParam, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }
    
}
