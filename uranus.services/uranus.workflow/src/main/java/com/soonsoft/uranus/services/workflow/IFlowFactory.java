package com.soonsoft.uranus.services.workflow;

import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public interface IFlowFactory<TFlowEngine extends IFlowEngine<?>, TFlowDefinition extends FlowDefinition<?>> {
    
    TFlowDefinition loadDefinition(Object parameter);

    TFlowEngine createEngine(TFlowDefinition definition);

}
