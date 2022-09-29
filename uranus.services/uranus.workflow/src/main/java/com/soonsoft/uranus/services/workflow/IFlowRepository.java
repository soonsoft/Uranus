package com.soonsoft.uranus.services.workflow;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public interface IFlowRepository<TFlowDefinition extends FlowDefinition<?>, TFlowState> {
    
    TFlowDefinition getDefinition(Object parameter);

    FlowNode<?> getCurrentNode();

    TFlowState getCurrentState();

    void create(FlowDefinition<?> definition, FlowActionParameter parameter);

    void saveState(TFlowState stateParam, FlowActionParameter parameter);

}
