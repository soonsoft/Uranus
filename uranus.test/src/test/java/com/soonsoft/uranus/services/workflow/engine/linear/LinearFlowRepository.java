package com.soonsoft.uranus.services.workflow.engine.linear;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class LinearFlowRepository implements IFlowRepository<LinearFlowDefinition, LinearFlowState> {

    @Override
    public LinearFlowDefinition getDefinition(String flowCode) {
        return null;
    }

    @Override
    public LinearFlowState getCurrentState(Object parameter) {
        return null;
    }

    @Override
    public void create(LinearFlowDefinition definition, FlowActionParameter parameter) {
        
    }

    @Override
    public void saveState(LinearFlowState stateParam, FlowActionParameter parameter) {
        
    }
    
}
