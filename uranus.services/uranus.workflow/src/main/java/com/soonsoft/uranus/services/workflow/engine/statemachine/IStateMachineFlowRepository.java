package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public interface IStateMachineFlowRepository<TFlowDefinition extends FlowDefinition<?>, TFlowState> 
    extends IFlowRepository<TFlowDefinition, TFlowState> {

    List<StateMachinePartialItem> getPartialItems(StateMachineFlowNode compositeNode, Object parameter);

}
