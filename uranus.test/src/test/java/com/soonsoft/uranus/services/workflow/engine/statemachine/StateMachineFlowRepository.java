package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class StateMachineFlowRepository 
        implements IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> {

    private Func1<Object, StateMachineFlowDefinition> definitionFn;
    private Func1<Object, StateMachineFlowState> currentStateFn;

    public void setDefinitionFn(Func1<Object, StateMachineFlowDefinition> definitionFn) {
        this.definitionFn = definitionFn;
    }

    public void setCurrentStateFn(Func1<Object, StateMachineFlowState> currentStateFn) {
        this.currentStateFn = currentStateFn;
    }

    @Override
    public StateMachineFlowDefinition getDefinition(String flowCode) {
        if(definitionFn == null) {
            return null;
        }
        return definitionFn.call(flowCode);
    }

    @Override
    public StateMachineFlowState getCurrentState(Object parameter) {
        if(currentStateFn == null) {
            return null;
        }
        return currentStateFn.call(parameter);
    }

    @Override
    public void create(StateMachineFlowDefinition definition, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveState(StateMachineFlowState newState, FlowActionParameter parameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<StateMachinePartialItem> getPratialItems(StateMachineCompositeNode compositeNode) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
