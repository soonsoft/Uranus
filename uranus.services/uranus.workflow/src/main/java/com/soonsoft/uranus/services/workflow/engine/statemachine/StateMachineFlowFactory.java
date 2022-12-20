package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate2;
import com.soonsoft.uranus.services.workflow.IFlowDefinitionBuilder;
import com.soonsoft.uranus.services.workflow.IFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowCancelState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineForkNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelNode;
import com.soonsoft.uranus.services.workflow.exception.FlowException;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public class StateMachineFlowFactory<TFlowQuery> 
                implements IFlowFactory<
                    StateMachineFLowEngine<TFlowQuery>, 
                    StateMachineFlowDefinition
                > {

    public static StateMachineFlowDefinitionSetter builder() {
        return new StateMachineFlowDefinitionSetter(new StateMachineFlowDefinition());
    }

    private IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> flowRepository;
    private TFlowQuery flowQuery;

    public StateMachineFlowFactory(
            IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> repository, 
            TFlowQuery query) {
        this.flowRepository = repository;
        this.flowQuery = query;
    }

    @Override
    public StateMachineFlowDefinitionSetter definitionBuilder() {
        return StateMachineFlowFactory.builder();
    }

    @Override
    public StateMachineFlowDefinition loadDefinition(Object parameter) {
        StateMachineFlowState state = getRepository().getCurrentState(parameter);
        if(state == null) {
            throw new FlowException("cannot find StateMachineFlowState by parameter[%s]", parameter);
        }
        // 这里必须是一个副本，definition每次需要承载状态，所以每个Flow Engine应该是单独一份
        StateMachineFlowDefinition definition = getRepository().getDefinition(state.getFlowCode());

        state.setFindFlowNodeFn(definition::findNode);

        definition.setPreviousNodeCode(state.getNodeCode());
        definition.setPreviousStateCode(state.getStateCode());
        definition.setCurrentNodeCode(state.getToNodeCode());

        StateMachineFlowNode previousNode = state.getFromNode();
        previousNode.setId(state.getFromNodeId());
        StateMachineFlowNode currentNode = state.getToNode();
        currentNode.setId(state.getToNodeId());

        if(StateMachineFlowCancelState.isCancelState(state.getStateCode())) {
            definition.setStatus(FlowStatus.Canceled);
        } else {
            if(currentNode instanceof StateMachineCompositeNode compositeNode) {
                List<StateMachinePartialItem> partialItemListWithState = getRepository().getPartialItems(compositeNode);
                compositeNode.setAllState(partialItemListWithState);
            } else if (currentNode instanceof StateMachineParallelNode parallelNode) {
                List<StateMachinePartialItem> partialItemListWithState = getRepository().getPartialItems(parallelNode);
                parallelNode.setAllState(partialItemListWithState);
            }

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

    public IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> getRepository() {
        return flowRepository;
    }

    protected void setRepository(IStateMachineFlowRepository<StateMachineFlowDefinition, StateMachineFlowState> repository) {
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

        public StateMachineFlowNodeSetter beginNode() {
            return node().setNodeType(StateMachineFlowNodeType.BeginNode);
        }

        public StateMachineFlowNodeSetter endNode() {
            return node().setNodeType(StateMachineFlowNodeType.EndNode);
        }

        public StateMachineCompositeNodeSetter compositeNode(Func1<StateMachineCompositeNode, String> resolveStateCodeFn) {
            return new StateMachineCompositeNodeSetter(this, get().createCompositeNode(resolveStateCodeFn));
        }

        public StateMachineForkNodeSetter forkNode() {
            return new StateMachineForkNodeSetter(this, get().createForkNode());
        }

        public StateMachineParallelNodeSetter parallelNode() {
            return new StateMachineParallelNodeSetter(this, get().createParallelNode());
        }

        public StateMachineFlowDefinition get() {
            return definition;
        }
    }

    //#region node

    public static abstract class BaseFlowNodeSetter<T extends BaseFlowNodeSetter<T, TNode>, TNode extends StateMachineFlowNode> {
        private final StateMachineFlowDefinitionSetter definitionSetter;
        private final TNode node;

        private BaseFlowNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, TNode node) {
            this.definitionSetter = definitionSetter;
            this.node = node;
        }

        public T setNodeType(StateMachineFlowNodeType nodeType) {
            node.setNodeType(nodeType);
            return self();
        }

        public T setNodeCode(String nodeCode) {
            node.setNodeCode(nodeCode);
            return self();
        }

        public T setNodeName(String nodeName) {
            node.setNodeName(nodeName);
            return self();
        }

        public StateMachineFlowDefinitionSetter add() {
            definitionSetter.get().addNode(node);
            return definitionSetter;
        }

        public TNode get() {
            return node;
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }
    }

    public static class StateMachineFlowNodeSetter extends BaseFlowNodeSetter<StateMachineFlowNodeSetter, StateMachineFlowNode> {
        private final StateMachineFlowDefinitionSetter definitionSetter;
        private StateMachineFlowNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, StateMachineFlowNode node) {
            super(definitionSetter, node);
            this.definitionSetter = definitionSetter;
        }

        public StateMachineFlowStateSetter<StateMachineFlowNodeSetter> state() {
            return new StateMachineFlowStateSetter<StateMachineFlowNodeSetter>(
                this, 
                definitionSetter.get().createFlowState(), 
                (nodeSetter, state) -> nodeSetter.get().addState(state)
            );
        }
    }

    public static class StateMachineCompositeNodeSetter 
            extends BaseFlowNodeSetter<StateMachineCompositeNodeSetter, StateMachineCompositeNode> {
        private final StateMachineFlowDefinitionSetter definitionSetter;
        public StateMachineCompositeNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, StateMachineCompositeNode node) {
            super(definitionSetter, node);
            this.definitionSetter = definitionSetter;
        }

        public StateMachineFlowStateSetter<StateMachineCompositeNodeSetter> state() {
            return new StateMachineFlowStateSetter<StateMachineCompositeNodeSetter>(
                this, 
                definitionSetter.get().createFlowState(),
                (nodeSetter, state) -> nodeSetter.get().addState(state)
            );
        }

        public StateMachinePartialItemSetter<StateMachineCompositeNodeSetter> partial() {
            return new StateMachinePartialItemSetter<StateMachineCompositeNodeSetter>(
                this, 
                new StateMachinePartialItem(),
                (nodeSetter, partialItem) -> nodeSetter.get().addPartialItem(partialItem)
            );
        }
    }

    public static class StateMachineForkNodeSetter extends BaseFlowNodeSetter<StateMachineForkNodeSetter, StateMachineForkNode> {
        private final StateMachineFlowDefinitionSetter definitionSetter;
        public StateMachineForkNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, StateMachineForkNode node) {
            super(definitionSetter, node);
            this.definitionSetter = definitionSetter;
        }

        public StateMachineGatewayStateSetter<StateMachineForkNodeSetter> state(Predicate2<Object, StateMachineForkNode> predicate) {
            return new StateMachineGatewayStateSetter<StateMachineForkNodeSetter>(
                this, 
                definitionSetter.get().createForkState(predicate),
                (nodeSetter, state) -> nodeSetter.get().addState(state)
            );
        }
    }

    public static class StateMachineParallelNodeSetter extends BaseFlowNodeSetter<StateMachineParallelNodeSetter, StateMachineParallelNode> {
        private final StateMachineFlowDefinitionSetter definitionSetter;
        public StateMachineParallelNodeSetter(StateMachineFlowDefinitionSetter definitionSetter, StateMachineParallelNode node) {
            super(definitionSetter, node);
            this.definitionSetter = definitionSetter;
        }

        public StateMachineGatewayStateSetter<StateMachineParallelNodeSetter> state(Predicate2<Object, StateMachineParallelNode> predicate) {
            return new StateMachineGatewayStateSetter<StateMachineParallelNodeSetter>(
                this, 
                definitionSetter.get().createParallelState(predicate),
                (nodeSetter, state) -> nodeSetter.get().addState(state)
            );
        }

        public ParallelPartialItemSetter partialNode() {
            return partialNode(
                definition -> definition.createFlowNode(),
                definition -> definition.createFlowState()
            );
        }

        public ParallelPartialItemSetter partialNode(
                Func1<StateMachineFlowDefinition, StateMachineFlowNode> nodeFactory,
                Func1<StateMachineFlowDefinition, StateMachineFlowState> stateFactory) {

            Guard.notNull(nodeFactory, "the parameter nodeFactory is required.");
            Guard.notNull(stateFactory, "the parameter stateFactory is required.");

            return new ParallelPartialItemSetter(
                this, 
                new StateMachinePartialItem(),
                nodeFactory.call(definitionSetter.get()),
                node -> definitionSetter.get().addNode(node),
                () -> stateFactory.call(definitionSetter.get()),
                partialItem -> this.get().addPartialItem(partialItem)
            );
        }
    }

    //#endregion

    //#region state

    public static abstract class BaseStateSetter<T extends BaseStateSetter<T, TNodeSetter>, TNodeSetter> {
        private final TNodeSetter nodeSetter;
        private final StateMachineFlowState state;
        private final Action2<TNodeSetter, StateMachineFlowState> addFn;

        private BaseStateSetter(TNodeSetter nodeSetter, StateMachineFlowState state, Action2<TNodeSetter, StateMachineFlowState> addFn) {
            this.nodeSetter = nodeSetter;
            this.state = state;
            this.addFn = addFn;
        }

        public T setStateCode(String stateCode) {
            state.setStateCode(stateCode);
            return self();
        }

        public T setStateName(String stateName) {
            state.setStateName(stateName);
            return self();
        }

        public T setToNodeCode(String nodeCode) {
            state.setToNodeCode(nodeCode);
            return self();
        }

        public TNodeSetter add() {
            addFn.apply(nodeSetter, state);
            return nodeSetter;
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }
    }

    public static class StateMachineFlowStateSetter<TNodeSetter> 
            extends BaseStateSetter<StateMachineFlowStateSetter<TNodeSetter>, TNodeSetter> {

        private StateMachineFlowStateSetter(TNodeSetter nodeSetter, StateMachineFlowState state, Action2<TNodeSetter, StateMachineFlowState> addFn) {
            super(nodeSetter, state, addFn);
        }

    }

    public static class StateMachineGatewayStateSetter<TNodeSetter> 
            extends BaseStateSetter<StateMachineGatewayStateSetter<TNodeSetter>, TNodeSetter> {

        public StateMachineGatewayStateSetter(TNodeSetter nodeSetter, StateMachineFlowState state, Action2<TNodeSetter, StateMachineFlowState> addFn) {
            super(nodeSetter, state, addFn);
        }
    }

    //#endregion

    //#region partial item

    public static class StateMachinePartialItemSetter<TNodeSetter> {

        private final StateMachinePartialItem partialItem;
        private final TNodeSetter nodeSetter;
        private final Action2<TNodeSetter, StateMachinePartialItem> addFn;

        public StateMachinePartialItemSetter(TNodeSetter nodeSetter, StateMachinePartialItem partialItem, Action2<TNodeSetter, StateMachinePartialItem> addFn) {
            this.partialItem = partialItem;
            this.nodeSetter = nodeSetter;
            this.addFn = addFn;
        }

        public StateMachinePartialItemSetter<TNodeSetter> setItemCode(String itemCode) {
            partialItem.setItemCode(itemCode);
            return this;
        }

        public StateMachinePartialItemSetter<TNodeSetter> setItemName(String itemName) {
            partialItem.setItemName(itemName);
            return this;
        }

        public TNodeSetter add() {
            addFn.apply(nodeSetter, partialItem);
            return nodeSetter;
        }

    }

    public static class ParallelPartialItemSetter {
        private final StateMachinePartialItem partialItem;
        private final StateMachineParallelNodeSetter nodeSetter;
        private final StateMachineFlowNode partialNode;
        private final Action1<StateMachineFlowNode> addNodeFn;
        private final Func0<StateMachineFlowState> createStateFn;
        private final Action1<StateMachinePartialItem> addItemFn;

        public ParallelPartialItemSetter(
                StateMachineParallelNodeSetter nodeSetter,
                StateMachinePartialItem partialItem,
                StateMachineFlowNode partialNode,
                Action1<StateMachineFlowNode> addNodeFn,
                Func0<StateMachineFlowState> createStateFn,
                Action1<StateMachinePartialItem> addItemFn) {
            this.partialItem = partialItem;
            this.nodeSetter = nodeSetter;
            this.partialNode = partialNode;
            this.addNodeFn = addNodeFn;
            this.createStateFn = createStateFn;
            this.addItemFn = addItemFn;
        }

        public ParallelPartialItemSetter setItemCode(String itemCode) {
            partialItem.setItemCode(itemCode);
            partialNode.setNodeCode(itemCode);
            return this;
        }

        public ParallelPartialItemSetter setItemName(String itemName) {
            partialItem.setItemName(itemName);
            partialNode.setNodeCode(itemName);
            return this;
        }

        public ParallelPartialItemSetter addState(String stateCode, String stateName) {
            StateMachineFlowState state = createStateFn.call();
            state.setStateCode(stateCode);
            state.setStateName(stateName);
            state.setToNodeCode(nodeSetter.get().getNodeCode());
            partialNode.addState(state);
            return this;
        }

        public StateMachineParallelNodeSetter add() {
            addItemFn.apply(partialItem);
            addNodeFn.apply(partialNode);
            return nodeSetter;
        }
        
    }

    //#endregion

    //#endregion
    
}
