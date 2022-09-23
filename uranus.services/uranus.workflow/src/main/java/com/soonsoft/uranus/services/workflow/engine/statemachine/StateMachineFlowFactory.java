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

    private IFlowRepository<StateMachineFlowState> flowRepository;
    private TFlowQuery flowQuery;

    public StateMachineFlowFactory(IFlowRepository<StateMachineFlowState> repository, TFlowQuery query) {
        this.flowRepository = repository;
        this.flowQuery = query;
    }

    @Override
    public StateMachineFlowDefinition loadDefinition(Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineFLowEngine<TFlowQuery> createEngine(StateMachineFlowDefinition definition) {
        StateMachineFLowEngine<TFlowQuery> engine = new StateMachineFLowEngine<>(definition, getFlowQuery());
        engine.setFlowRepository(getRepository());
        return engine;
    }

    public IFlowRepository<StateMachineFlowState> getRepository() {
        return flowRepository;
    }

    protected void setRepository(IFlowRepository<StateMachineFlowState> repository) {
        this.flowRepository = repository;
    }

    public TFlowQuery getFlowQuery() {
        return flowQuery;
    }

    protected void setFlowQuery(TFlowQuery flowQuery) {
        this.flowQuery = flowQuery;
    }
    
}
