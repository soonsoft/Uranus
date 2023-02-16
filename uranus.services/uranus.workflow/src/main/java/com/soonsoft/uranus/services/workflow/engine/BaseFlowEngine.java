package com.soonsoft.uranus.services.workflow.engine;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.event.IEventListener;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.services.workflow.IFlowEngine;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowState;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;
import com.soonsoft.uranus.services.workflow.model.event.FlowActionEvent;
import com.soonsoft.uranus.services.workflow.model.event.FlowStatusChangedEvent;

public abstract class BaseFlowEngine<
            TFlowDefinition extends FlowDefinition<?>, 
            TFlowState extends FlowState, 
            TRepository, 
            TFlowQuery
        > implements IFlowEngine<TFlowState, TFlowQuery> {

    private TFlowDefinition definition;
    private TFlowQuery flowQuery;
    private TRepository flowRepository;

    private final IEventListener<FlowStatusChangedEvent<TFlowDefinition>> flowStatusChangedEvent;
    private final IEventListener<FlowActionEvent> flowActionEvent;

    public BaseFlowEngine(TFlowDefinition definition) {
        this(definition, null, null, null);
    }

    public BaseFlowEngine(TFlowDefinition definition, TFlowQuery query) {
        this(definition, query, null, null);
    }

    public BaseFlowEngine(
        TFlowDefinition definition, 
        TFlowQuery query,
        Func0<IEventListener<FlowStatusChangedEvent<TFlowDefinition>>> createFlowStatusChangedEventFn,
        Func0<IEventListener<FlowActionEvent>> createFlowActionEventFn) {

        this.definition = definition;
        this.flowQuery = query;

        if(createFlowStatusChangedEventFn != null) {
            flowStatusChangedEvent = createFlowStatusChangedEventFn.call();
        } else {
            flowStatusChangedEvent = new SimpleEventListener<>("FlowStatusChangedEvent");
        }

        if(createFlowActionEventFn != null) {
            flowActionEvent = createFlowActionEventFn.call();
        } else {
            flowActionEvent = new SimpleEventListener<>("FlowActionEvent");
        }
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

    //#region Events

    public IEventListener<FlowStatusChangedEvent<TFlowDefinition>> getFlowStatusChangedEvent() {
        return flowStatusChangedEvent;
    }

    public IEventListener<FlowActionEvent> getFlowActionEvent() {
        return flowActionEvent;
    }

    //#endregion

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

    public TFlowState action(String nodeCode, String stateCode) {
        return action(nodeCode, stateCode, null);
    }

    protected TFlowDefinition getDefinition() {
        return this.definition;
    }

    protected void prepareStart(Object parameter) {
        if(getStatus() != FlowStatus.Pending) {
            throw new FlowException(
                "start flow process error, cause the status of flow process is incurrect, current status is [%s]", 
                getStatus().name());
        }
    }

    protected void prepareAction(String nodeCode, FlowActionParameter parameter) {
        if(!isStarted()) {
            throw new FlowException(
                "the flow process do action error, cause the status is incurrect, current status is [%s]", 
                getStatus().name());
        }

        Guard.notEmpty(nodeCode, "the parameter nodeCode is required.");
    }

    protected void prepareAction(String nodeCode, String stateCode, FlowActionParameter parameter) {
        prepareAction(nodeCode, parameter);
        Guard.notEmpty(stateCode, "the parameter stateCode is required.");
    }

    protected void prepareCancel(FlowActionParameter parameter) {
        if(!getDefinition().isCancelable()) {
            throw new FlowException("the flow definition [%s] is not supported", definition.getFlowName());
        }

        FlowStatus status = getStatus();
        if(status == FlowStatus.Canceled || status == FlowStatus.Finished) {
            throw new FlowException("the flow process is done, current status is [%s]", status.name());
        }
    }
    
    protected void onFlowStatusChangedEvent(FlowStatusChangedEvent<TFlowDefinition> event) {
        if(flowStatusChangedEvent != null) {
            Guard.notNull(event, "the parameter event is required.");
            event.setName(flowStatusChangedEvent.getName());
            flowStatusChangedEvent.trigger(event);
        }
    }

    protected void onFlowActionEvent(FlowActionEvent event) {
        if(flowActionEvent != null) {
            Guard.notNull(event, "the parameter event is required.");
            event.setName(flowActionEvent.getName());
            flowActionEvent.trigger(event);
        }
    }
}
