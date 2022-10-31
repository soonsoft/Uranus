package com.soonsoft.uranus.services.workflow.engine.statemachine;

import com.soonsoft.uranus.services.workflow.IFlowDefinitionBuilder;
import com.soonsoft.uranus.services.workflow.IFlowFactory;
import com.soonsoft.uranus.services.workflow.IFlowRepository;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

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
        if(state == null) {
            throw new FlowException("cannot find StateMachineFlowState by parameter[%s]", parameter);
        }
        StateMachineFlowDefinition definition = getRepository().getDefinition(state.getFlowCode());

        state.setFindFlowNodeFn(definition::findNode);

        definition.setPreviousNodeCode(state.getNodeCode());
        definition.setPreviousStateCode(state.getStateCode());
        definition.setCurrentNodeCode(state.getToNodeCode());

        if(StateMachineFlowCancelState.isCancelState(state.getStateCode())) {
            definition.setStatus(FlowStatus.Canceled);
        } else {
        StateMachineFlowNode currentNode = state.getToNode();
            if(currentNode.isBeginNode() || currentNode.getNodeType() == StateMachineFlowNodeType.NormalNode) {
                definition.setStatus(FlowStatus.Started);
            } else if(currentNode.isEndNode()) {
                definition.setStatus(FlowStatus.Finished);
            }
        }
        
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
