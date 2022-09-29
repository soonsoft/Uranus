package com.soonsoft.uranus.services.workflow.engine.statemachine;

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
    public StateMachineFlowDefinition loadDefinition(Object parameter) {
        return getRepository().getDefinition(parameter);
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
    
}
