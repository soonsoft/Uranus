package com.soonsoft.uranus.services.workflow.engine.linear;

import java.util.List;

import com.soonsoft.uranus.core.error.UnsupportedException;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowNodeState;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowResult;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public interface ILinearFlowRepository extends IFlowRepository<LinearFlowDefinition, LinearFlowState> {

    @Override
    default void saveState(LinearFlowState stateParam, FlowActionParameter parameter) {
        saveState((LinearFlowResult) stateParam, parameter);
    }

    void saveState(LinearFlowResult result, FlowActionParameter parameter);

    @Override
    default LinearFlowState getCurrentState(Object parameter) {
        throw new UnsupportedException("unsupported");
    }

    List<LinearFlowNodeState> getCurrentNodeStates(Object parameter);
    
}
