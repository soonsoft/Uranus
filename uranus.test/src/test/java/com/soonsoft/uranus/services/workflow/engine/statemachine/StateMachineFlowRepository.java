package com.soonsoft.uranus.services.workflow.engine.statemachine;

import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowRepository implements IFlowRepository<StateMachineFlowState> {

    @Override
    public FlowDefinition<?> getDefinition(Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FlowNode<?> getCurrentNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineFlowState getCurrentState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(FlowDefinition<?> definition, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveState(StateMachineFlowState newState, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }
    
}
