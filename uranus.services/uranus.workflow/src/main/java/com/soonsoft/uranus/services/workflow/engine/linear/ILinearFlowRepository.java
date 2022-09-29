package com.soonsoft.uranus.services.workflow.engine.linear;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowResult;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public interface ILinearFlowRepository extends IFlowRepository<LinearFlowDefinition, LinearFlowState> {

    @Override
    default void saveState(LinearFlowState stateParam, FlowActionParameter parameter) {
        saveState((LinearFlowResult) stateParam, parameter);
    }

    void saveState(LinearFlowResult result, FlowActionParameter parameter);
    
}
