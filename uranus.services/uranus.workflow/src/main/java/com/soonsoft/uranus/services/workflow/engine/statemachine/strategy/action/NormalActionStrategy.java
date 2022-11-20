package com.soonsoft.uranus.services.workflow.engine.statemachine.strategy.action;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.strategy.IFlowActionStrategy;
import com.soonsoft.uranus.services.workflow.exception.FlowException;

public class NormalActionStrategy implements IFlowActionStrategy {

    @Override
    public StateMachineFlowNode getActionFlowNode(StateMachineFlowDefinition definition,
            StateMachineFlowNode currentNode, String actionNodeCode) {
        
        if(!currentNode.getNodeCode().equals(actionNodeCode)) {
            throw new FlowException(
                "the current node code of definition is [%s], but the parameter nodeCode is [%s]", 
                definition.getCurrentNodeCode(), actionNodeCode);
        }
        return currentNode;
    }
    
}
