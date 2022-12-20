package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.ParallelActionNodeState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
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
        System.out.println("\n");
        System.out.println(
            StringUtils.format(
                "[Create - {0}]", definition.getFlowCode())
        );
    }

    @Override
    public void saveState(StateMachineFlowState newState, FlowActionParameter parameter) {
        if(newState instanceof ParallelActionNodeState actionState) {
            showState(actionState);
        } else if(newState instanceof CompositionPartialState partialState) {
            showState(partialState);
        } else {
            showState(newState);
        }
    }

    @Override
    public List<StateMachinePartialItem> getPartialItems(StateMachineFlowNode compositeNode) {
        // TODO Auto-generated method stub
        return null;
    }

    private void showState(StateMachineFlowState newState) {
        System.out.println(
            StringUtils.format(
                "[Action - {0}]: {1}.{2} > {3}", 
                newState.getFlowCode(),
                newState.getNodeCode(), newState.getStateCode(), newState.getToNodeCode())
        );
    }

    private void showState(CompositionPartialState newState) {
        System.out.println(
            StringUtils.format(
                "[Action- {0}]: {1}.{2} (Continue)", 
                newState.getFlowCode(), newState.getNodeCode(), newState.getStateCode())
        );
    }

    private void showState(ParallelActionNodeState newState) {
        System.out.println(
            StringUtils.format(
                "[Action - {0}]: {1} ~ {2}.{3} > (Continue)", 
                newState.getFlowCode(), newState.getNodeCode(), 
                newState.getActionNodeCode(), newState.getStateCode())
        );
    }
    
}
