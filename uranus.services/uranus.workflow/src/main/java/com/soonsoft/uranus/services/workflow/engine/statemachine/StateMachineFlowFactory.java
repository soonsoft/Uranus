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
    public StateMachineFlowDefinitionSetter definitionBuilder() {
        return new StateMachineFlowDefinitionSetter(new StateMachineFlowDefinition());
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

    //#region definition builder

    public static class StateMachineFlowDefinitionSetter implements IFlowDefinitionBuilder<StateMachineFlowDefinition> {

        private StateMachineFlowDefinition definition;

        private StateMachineFlowDefinitionSetter(StateMachineFlowDefinition definition) {
            this.definition = definition;
        }

        @Override
        public StateMachineFlowDefinition build() {
            return get();
        }

        public StateMachineFlowDefinitionSetter setFlowCode(String flowCode) {
            definition.setFlowCode(flowCode);
            return this;
        }

        public StateMachineFlowDefinitionSetter setFlowName(String name) {
            definition.setFlowName(name);
            return this;
        }

        public StateMachineFlowDefinitionSetter setFlowType(String flowType) {
            definition.setFlowType(flowType);
            return this;
        }

        public StateMachineFlowDefinitionSetter setCancelable(boolean cancelable) {
            definition.setCancelable(cancelable);
            return this;
        }

        public StateMachineFlowDefinitionSetter setDescription(String desc) {
            definition.setDescription(desc);
            return this;
        }

        public StateMachineFlowNodeSetter node() {
            return new StateMachineFlowNodeSetter(this, get().createFlowNode());
        }

        private StateMachineFlowDefinition get() {
            return definition;
        }
    }

    public static class StateMachineFlowNodeSetter {

        private StateMachineFlowDefinitionSetter definitionSetter;
        private StateMachineFlowNode node;

        private StateMachineFlowNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, StateMachineFlowNode node) {
            this.definitionSetter = definitionSetter;
            this.node = node;
        }

        public StateMachineFlowNodeSetter setNodeType(StateMachineFlowNodeType nodeType) {
            node.setNodeType(nodeType);
            return this;
        }

        public StateMachineFlowNodeSetter setNodeCode(String nodeCode) {
            node.setNodeCode(nodeCode);
            return this;
        }

        public StateMachineFlowNodeSetter setNodeName(String nodeName) {
            node.setNodeName(nodeName);
            return this;
        }

        public StateMachineFlowStateSetter state() {
            return new StateMachineFlowStateSetter(this, definitionSetter.get().createFlowState());
        }

        public StateMachineFlowDefinitionSetter add() {
            definitionSetter.get().addNode(node);
            return definitionSetter;
        }

        private StateMachineFlowNode get() {
            return node;
        }

    }

    public static class StateMachineFlowStateSetter {

        private StateMachineFlowNodeSetter nodeSetter;
        private StateMachineFlowState state;

        private StateMachineFlowStateSetter(StateMachineFlowNodeSetter nodeSetter, StateMachineFlowState state) {
            this.nodeSetter = nodeSetter;
            this.state = state;
        }

        public StateMachineFlowStateSetter setStateCode(String stateCode) {
            state.setStateCode(stateCode);
            return this;
        }

        public StateMachineFlowStateSetter setStateName(String stateName) {
            state.setStateName(stateName);
            return this;
        }

        public StateMachineFlowStateSetter setToNodeCode(String nodeCode) {
            state.setToNodeCode(nodeCode);
            return this;
        }

        public StateMachineFlowNodeSetter add() {
            nodeSetter.get().addState(state);
            return nodeSetter;
        }

    }

    //#endregion
    
}
