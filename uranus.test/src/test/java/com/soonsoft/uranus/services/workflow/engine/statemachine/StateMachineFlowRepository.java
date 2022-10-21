package com.soonsoft.uranus.services.workflow.engine.statemachine;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowRepository 
        implements IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {

    private Func1<Object, StateMachineFlowDefinition> definitionFn;

    public void setDefinitionFn(Func1<Object, StateMachineFlowDefinition> definitionFn) {
        this.definitionFn = definitionFn;
    }

    @Override
    public StateMachineFlowDefinition getDefinition(Object parameter) {
        if(definitionFn == null) {
            return null;
        }
        return definitionFn.call(parameter);
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
    public void create(StateMachineFlowDefinition definition, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveState(StateMachineFlowState newState, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }
    
}
