package com.soonsoft.uranus.services.workflow;

import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowState;

public interface IFlowFactory<
        TFlowEngine extends IFlowEngine<? extends FlowState, ?>, 
        TFlowDefinition extends FlowDefinition<?>
    > {

    IFlowDefinitionBuilder<TFlowDefinition> definitionBuilder();
    
    TFlowDefinition loadDefinition(Object parameter);

    TFlowEngine createEngine(TFlowDefinition definition);

}
