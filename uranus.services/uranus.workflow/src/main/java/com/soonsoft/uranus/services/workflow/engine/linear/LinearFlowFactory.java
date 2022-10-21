package com.soonsoft.uranus.services.workflow.engine.linear;

import com.soonsoft.uranus.services.workflow.IFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;

public class LinearFlowFactory<TFlowQuery> 
        implements IFlowFactory<LinearFlowEngine<TFlowQuery>, LinearFlowDefinition> {

    private ILinearFlowRepository flowRepository;
    private TFlowQuery flowQuery;

    public LinearFlowFactory(ILinearFlowRepository flowRepository, TFlowQuery flowQuery) {
        this.flowRepository = flowRepository;
        this.flowQuery = flowQuery;
    }

    public ILinearFlowRepository getFlowRepository() {
        return flowRepository;
    }

    protected void setFlowRepository(ILinearFlowRepository flowRepository) {
        this.flowRepository = flowRepository;
    }

    public TFlowQuery getFlowQuery() {
        return flowQuery;
    }

    protected void setFlowQuery(TFlowQuery flowQuery) {
        this.flowQuery = flowQuery;
    }

    @Override
    public LinearFlowDefinition loadDefinition(Object parameter) {
        return getFlowRepository().getDefinition(parameter);
    }

    @Override
    public LinearFlowEngine<TFlowQuery> createEngine(LinearFlowDefinition definition) {
        LinearFlowEngine<TFlowQuery> engine = new LinearFlowEngine<>(definition, getFlowQuery());
        engine.setFlowRepository(getFlowRepository());
        return engine;
    }
    
}
