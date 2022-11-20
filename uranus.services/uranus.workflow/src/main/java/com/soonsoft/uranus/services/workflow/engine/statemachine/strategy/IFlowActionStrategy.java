package com.soonsoft.uranus.services.workflow.engine.statemachine.strategy;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;

public interface IFlowActionStrategy {

    StateMachineFlowNode getActionFlowNode(StateMachineFlowDefinition definition, StateMachineFlowNode currentNode, String actionNodeCode);
    
}
