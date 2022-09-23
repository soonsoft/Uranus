package com.soonsoft.uranus.services.workflow.engine;

import com.soonsoft.uranus.services.workflow.IFlowEngine;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public abstract class BaseFlowEngine<TFlowDefinition extends FlowDefinition<?>, TRepository, TFlowQuery> 
                        implements IFlowEngine<TFlowQuery> {

    private TFlowDefinition definition;
    private TFlowQuery flowQuery;
    private TRepository flowRepository;

    public BaseFlowEngine(TFlowDefinition definition) {
        this(definition, null);
    }

    public BaseFlowEngine(TFlowDefinition definition, TFlowQuery query) {
        this.definition = definition;
        this.flowQuery = query;
    }

    public TFlowQuery getFlowQuery() {
        return flowQuery;
    }

    public TRepository getFlowRepository() {
        return flowRepository;
    }

    public void setFlowRepository(TRepository flowRepository) {
        this.flowRepository = flowRepository;
    }

    @Override
    public FlowStatus getStatus() {
        return definition.getStatus();
    }

    @Override
    public boolean isStarted() {
        return getStatus() == FlowStatus.Started;
    }

    @Override
    public boolean isFinished() {
        return getStatus() == FlowStatus.Finished;
    }

    @Override
    public boolean isCanceled() {
        return getStatus() == FlowStatus.Canceled;
    }

    @Override
    public TFlowQuery query() {
        return this.flowQuery;
    }

    public void start() {
        start(null);
    }

    public StateMachineFlowState action(String nodeCode, String stateCode) {
        return action(nodeCode, stateCode, null);
    }

    protected TFlowDefinition getDefinition() {
        return this.definition;
    }
    
}
