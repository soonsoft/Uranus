package com.soonsoft.uranus.services.workflow.engine.statemachine;

import com.soonsoft.uranus.services.workflow.IFlowDefinitionBuilder;
import com.soonsoft.uranus.services.workflow.IFlowFactory;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;

public class StateMachineFlowFactory<TFlowQuery> 
                implements IFlowFactory<
                    StateMachineFLowEngine<TFlowQuery>, 
                    StateMachineFlowDefinition
                > {

    private IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> flowRepository;
    private TFlowQuery flowQuery;

    public StateMachineFlowFactory(
            IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> repository, 
            TFlowQuery query) {
        this.flowRepository = repository;
        this.flowQuery = query;
    }

    @Override
    public StateMachineFlowDefinitionBuilder definitionBuilder() {
        return new StateMachineFlowDefinitionBuilder();
    }

    @Override
    public StateMachineFlowDefinition loadDefinition(Object parameter) {
        StateMachineFlowState state = getRepository().getCurrentState(parameter);
        StateMachineFlowDefinition definition = getRepository().getDefinition(state.getFlowCode());

        state.setFindFlowNodeFn(definition::findNode);
        definition.setPreviousNodeCode(state.getNodeCode());
        definition.setPreviousStateCode(state.getStateCode());
        definition.setCurrentNodeCode(state.getToNodeCode());
        
        return definition;
    }

    @Override
    public StateMachineFLowEngine<TFlowQuery> createEngine(StateMachineFlowDefinition definition) {
        StateMachineFLowEngine<TFlowQuery> engine = new StateMachineFLowEngine<>(definition, getFlowQuery());
        engine.setFlowRepository(getRepository());
        return engine;
    }

    public IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> getRepository() {
        return flowRepository;
    }

    protected void setRepository(IFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> repository) {
        this.flowRepository = repository;
    }

    public TFlowQuery getFlowQuery() {
        return flowQuery;
    }

    protected void setFlowQuery(TFlowQuery flowQuery) {
        this.flowQuery = flowQuery;
    }

    public static class StateMachineFlowDefinitionBuilder implements IFlowDefinitionBuilder<StateMachineFlowDefinition> {

        @Override
        public StateMachineFlowDefinition build() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
}
