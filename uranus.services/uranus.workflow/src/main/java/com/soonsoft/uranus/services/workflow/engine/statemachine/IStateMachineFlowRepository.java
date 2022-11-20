package com.soonsoft.uranus.services.workflow.engine.statemachine;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public interface IStateMachineFlowRepository<TFlowDefinition extends FlowDefinition<?>, TFlowState> 
    extends IFlowRepository<TFlowDefinition, TFlowState> {

    
    
}
