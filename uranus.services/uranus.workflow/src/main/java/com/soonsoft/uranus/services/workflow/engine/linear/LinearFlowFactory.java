package com.soonsoft.uranus.services.workflow.engine.linear;

import java.util.List;

import com.soonsoft.uranus.services.workflow.IFlowDefinitionBuilder;
import com.soonsoft.uranus.services.workflow.IFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowNode;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowStatus;

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
    public LinearFlowDefinitionSetter definitionBuilder() {
        LinearFlowDefinition definition = new LinearFlowDefinition();
        return new LinearFlowDefinitionSetter(definition);
    }

    @Override
    public LinearFlowDefinition loadDefinition(Object parameter) {
        LinearFlowState state = getFlowRepository().getCurrentState(parameter);
        LinearFlowDefinition definition = getFlowRepository().getDefinition(state.getFlowCode());

        state.setActionFn(null);
        // TODO 未完成
        List<LinearFlowNode> nodeList = definition.findNode(n -> n.getNodeCode().equals(state.getNodeCode()));
        nodeList.forEach(n -> n.setNodeStatus(LinearFlowStatus.Activated));
        return definition;
    }

    @Override
    public LinearFlowEngine<TFlowQuery> createEngine(LinearFlowDefinition definition) {
        LinearFlowEngine<TFlowQuery> engine = new LinearFlowEngine<>(definition, getFlowQuery());
        engine.setFlowRepository(getFlowRepository());
        return engine;
    }

    //#region definition builder

    public static class LinearFlowDefinitionSetter implements IFlowDefinitionBuilder<LinearFlowDefinition> {

        private LinearFlowDefinition definition;

        private LinearFlowDefinitionSetter(LinearFlowDefinition definition) {
            this.definition = definition;
        }

        @Override
        public LinearFlowDefinition build() {
            return get();
        }

        public LinearFlowNodeSetter node() {
            LinearFlowNode node = definition.createNode();
            return new LinearFlowNodeSetter(this, node);
        }

        private LinearFlowDefinition get() {
            return this.definition;
        }

    }

    public static class LinearFlowNodeSetter {

        private LinearFlowDefinitionSetter definitionSetter;
        private LinearFlowNode node;

        private LinearFlowNodeSetter(LinearFlowDefinitionSetter definitionSetter, LinearFlowNode node) {
            this.definitionSetter = definitionSetter;
            this.node = node;
        }

        public LinearFlowDefinitionSetter add() {
            definitionSetter.get().addNode(node);
            return definitionSetter;
        }

        public LinearFlowStateSetter state() {
            LinearFlowState state = definitionSetter.get().createState();
            return new LinearFlowStateSetter(this, state);
        }

        private LinearFlowNode get() {
            return this.node;
        }

    }

    public static class LinearFlowStateSetter {

        private LinearFlowNodeSetter nodeSetter;
        private LinearFlowState state;

        private LinearFlowStateSetter(LinearFlowNodeSetter nodeSetter, LinearFlowState state) {
            this.nodeSetter = nodeSetter;
            this.state = state;
        }

        public LinearFlowNodeSetter add() {
            nodeSetter.get().addState(state);
            return nodeSetter;
        }

    }

    //#endregion
    
}
