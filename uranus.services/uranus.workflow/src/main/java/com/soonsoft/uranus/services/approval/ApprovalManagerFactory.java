package com.soonsoft.uranus.services.approval;

import java.util.HashMap;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;
import com.soonsoft.uranus.services.approval.simple.SimpleApprovalManager;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory.StateMachineCompositeNodeSetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory.StateMachineFlowDefinitionSetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory.StateMachineFlowNodeSetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.util.identity.ID;

public class ApprovalManagerFactory<TApprovalQuery> {

    public IApprovalManager<TApprovalQuery> createManager(
            TApprovalQuery query, 
            IApprovalRepository approvalRepository,
            ApprovalDefinitionContainer definitionContainer) {

        return createManager(query, approvalRepository, () -> ID.newGuid(), definitionContainer);

    }

    public IApprovalManager<TApprovalQuery> createManager(
            TApprovalQuery query, 
            IApprovalRepository approvalRepository,
            Func0<String> codeGenerator, 
            ApprovalDefinitionContainer definitionContainer) {

        SimpleApprovalManager<TApprovalQuery> defaultApprovalManager = 
            new SimpleApprovalManager<>(query, approvalRepository, codeGenerator, type -> definitionContainer.get(type));
        return defaultApprovalManager;
    }

    public ApprovalDefinitionContainer definitionContainer() {
        return definitionContainer(stateCode -> stateCode);
    }

    public ApprovalDefinitionContainer definitionContainer(Func1<String, String> stateNameGetter) {
        ApprovalDefinitionContainer contianer = new ApprovalDefinitionContainer();
        contianer.setStateNameGetter(stateNameGetter);
        return contianer;
    }

    //#region DefinitionContainer 构建器

    public static abstract class BaseDefinitionContainer<T extends BaseDefinitionContainer<T>> {
        private final Map<String, StateMachineFlowDefinition> map;
        private final ApprovalDefinitionBuildingContext<T> context;

        public BaseDefinitionContainer() {
            this.map = new HashMap<>();
            this.context = new ApprovalDefinitionBuildingContext<>(self());
        }

        public T define(String approvalType, Func1<StateMachineFlowDefinitionSetter, StateMachineFlowDefinition> factory) {
            return define(approvalType, factory.call(StateMachineFlowFactory.builder()));
        }

        public T define(String approvalType, StateMachineFlowDefinition definition) {
            Guard.notEmpty(approvalType, "the parameter approvalType is required.");
            Guard.notNull(definition, "the parameter definition is required.");
            if(map.containsKey(approvalType)) {
                throw new ApprovalException("the approvalType [%s] is exists.", approvalType);
            }

            map.put(approvalType, definition);

            return self();
        }

        public StateMachineFlowDefinition getCopy(String approvalType) {
            StateMachineFlowDefinition prototypeDefinition = get(approvalType);
            return prototypeDefinition.copy();
        }

        public StateMachineFlowDefinition get(String approvalType) {
            return map.get(approvalType);
        }

        public String getBeginNodeCode(String approvalType) {
            if(!map.containsKey(approvalType)) {
                throw new IllegalArgumentException("the approvalType not exists.");
            }

            StateMachineFlowDefinition definition = map.get(approvalType);
            StateMachineFlowNode beginNode = definition.getBeginNode();
            return beginNode != null ? beginNode.getNodeCode() : null;
        }

        public String getEndNodeCode(String approvalType) {
            if(!map.containsKey(approvalType)) {
                throw new IllegalArgumentException("the approvalType not exists.");
            }

            StateMachineFlowDefinition definition = map.get(approvalType);
            StateMachineFlowNode endNode = definition.getEndNode();
            return endNode != null ? endNode.getNodeCode() : null;
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }

        private void put(String approvalType, StateMachineFlowDefinition definition) {
            map.put(approvalType, definition);
        }

        private ApprovalDefinitionBuildingContext<T> getContext() {
            return context;
        }
    }

    public static class ApprovalDefinitionBuildingContext<TContainer> {
        private final TContainer container;
        private StateMachineFlowDefinitionSetter currentDefinitionSetter;
        private ApprovalNodeSetter<TContainer> currentNodeSetter;
        private Func1<String, String> stateNameGetter;

        public ApprovalDefinitionBuildingContext(TContainer container) {
            this.container = container;
        }

        public TContainer getContainer() {
            return container;
        }

        public StateMachineFlowDefinitionSetter getCurrentDefinitionSetter() {
            return currentDefinitionSetter;
        }

        public void setCurrentDefinitionSetter(StateMachineFlowDefinitionSetter currentDefinitionSetter) {
            this.currentDefinitionSetter = currentDefinitionSetter;
        }

        public ApprovalNodeSetter<TContainer> getCurrentNodeSetter() {
            return currentNodeSetter;
        }

        public void setCurrentNodeSetter(ApprovalNodeSetter<TContainer> currentNodeSetter) {
            this.currentNodeSetter = currentNodeSetter;
        }

        public Func1<String, String> getStateNameGetter() {
            return stateNameGetter;
        }

        public void setStateNameGetter(Func1<String, String> stateNameGetter) {
            this.stateNameGetter = stateNameGetter;
        }
    }

    public static enum ApprovalNodeType {
        /** 普通节点 */
        Normal,
        /** 会签 */
        All,
        /** 或签 */
        Any,
        ;
    }

    public static class ApprovalDefinitionContainer extends BaseDefinitionContainer<ApprovalDefinitionContainer> {
        
        public ApprovalDefinitionBuilder<ApprovalDefinitionContainer> definition(String approvalType, String flowName, boolean cancelable) {
            StateMachineFlowDefinitionSetter flowDefinitionSetter = 
                StateMachineFlowFactory.builder()
                    .setFlowCode(approvalType) // FlowCode 就是 approvalType
                    .setFlowName(flowName)
                    .setCancelable(cancelable)
                    .setFlowType(approvalType);
            
            super.getContext().setCurrentDefinitionSetter(flowDefinitionSetter);
            super.put(approvalType, flowDefinitionSetter.get());

            ApprovalDefinitionBuilder<ApprovalDefinitionContainer> definitionBuilder = new ApprovalDefinitionBuilder<>(super.getContext());
            return definitionBuilder;
        }

        public void setStateNameGetter(Func1<String, String> stateNameGetter) {
            super.getContext().setStateNameGetter(stateNameGetter);
        }

    }

    public static class ApprovalDefinitionBuilder<TContainer> {

        private final ApprovalDefinitionBuildingContext<TContainer> buildingContext;

        public ApprovalDefinitionBuilder(ApprovalDefinitionBuildingContext<TContainer> buildingContext) {
            this.buildingContext = buildingContext;
        }

        public ApprovalNodeSetter<TContainer> begin() {
            return begin("#ApprovalBeginNode", "Begin");
        }

        public ApprovalNodeSetter<TContainer> begin(String beginNodeCode, String beginNodeName) {
            StateMachineFlowDefinitionSetter flowDefinitionSetter = buildingContext.getCurrentDefinitionSetter();
            StateMachineFlowNodeSetter nodeSetter = 
                flowDefinitionSetter.beginNode()
                    .setNodeCode(beginNodeCode)
                    .setNodeName(beginNodeName);
            
            StateMachineFlowNode beginNode = nodeSetter.get();
            nodeSetter.add();
            return new ApprovalNodeSetter<TContainer>(buildingContext, beginNode, beginNode);
        }

    }

    public static class ApprovalNodeSetter<TContainer> {

        private final ApprovalDefinitionBuildingContext<TContainer> buildingContext;
        private final StateMachineFlowNode beginNode;
        private final StateMachineFlowNode previousNode;

        public ApprovalNodeSetter(
                ApprovalDefinitionBuildingContext<TContainer> buildingContext, 
                StateMachineFlowNode beginNode,
                StateMachineFlowNode previousNode) {
            this.buildingContext = buildingContext;
            this.beginNode = beginNode;
            this.previousNode = previousNode;
        }

        public ApprovalNodeSetter<TContainer> next(String nodeCode, String nodeName) {
            StateMachineFlowDefinitionSetter definitionSetter = buildingContext.getCurrentDefinitionSetter();
            StateMachineFlowNodeSetter nodeSetter = definitionSetter.node();
            StateMachineFlowNode node = nodeSetter
                    .setNodeCode(nodeCode)
                    .setNodeName(nodeName)
                    .state()
                        .setStateCode(ApprovalStateCode.Denied)
                        .setStateName(buildingContext.getStateNameGetter().call(ApprovalStateCode.Denied))
                        .setToNodeCode(beginNode.getNodeCode())
                        .add()
                    .get();
            nodeSetter.add();

            return createNextSetter(node);
        }

        public ApprovalNodeSetter<TContainer> next(
                String nodeCode, String nodeName, ApprovalNodeType nodeType, Action1<StateMachineCompositeNodeSetter> addPartial) {

            StateMachineFlowDefinitionSetter definitionSetter = buildingContext.getCurrentDefinitionSetter();
            StateMachineCompositeNodeSetter nodeSetter;
            if(nodeType == ApprovalNodeType.All) {
                nodeSetter = definitionSetter.compositeNode(node -> node.resolveState(ApprovalStateCode.Approved, ApprovalStateCode.Denied));
            } else if(nodeType == ApprovalNodeType.Any) {
                nodeSetter = definitionSetter.compositeNode(node -> node.resolveState(ApprovalStateCode.Denied, ApprovalStateCode.Approved));
            } else {
                return next(nodeCode, nodeName);
            }
            
            StateMachineCompositeNode node = nodeSetter
                .setNodeCode(nodeCode)
                .setNodeName(nodeName)
                .setNodeType(StateMachineFlowNodeType.NormalNode)
                    .state()
                        .setStateCode(ApprovalStateCode.Denied)
                        .setStateName(buildingContext.getStateNameGetter().call(ApprovalStateCode.Denied))
                        .setToNodeCode(beginNode.getNodeCode())
                        .add()
                .get();
            if(addPartial != null) {
                addPartial.apply(nodeSetter);
            }
            nodeSetter.add();

            return createNextSetter(node);
        }

        public TContainer end() {
            return end("#ApprovalEndNode", "End");
        }

        public TContainer end(String nodeCode, String nodeName) {
            StateMachineFlowDefinitionSetter definitionSetter = buildingContext.getCurrentDefinitionSetter();
            definitionSetter
                .endNode()
                    .setNodeCode(nodeCode)
                    .setNodeName(buildingContext.getStateNameGetter().call(nodeCode))
                    .add();
                
            if(previousNode != null) {
                linkState(
                    definitionSetter.get().createFlowState(), 
                    previousNode, 
                    ApprovalStateCode.Approved, 
                    nodeCode);
            }
            return buildingContext.getContainer();
        }

        private ApprovalNodeSetter<TContainer> createNextSetter(StateMachineFlowNode node) {
            if(previousNode != null) {
                StateMachineFlowDefinitionSetter definitionSetter = buildingContext.getCurrentDefinitionSetter();
                linkState(
                    definitionSetter.get().createFlowState(), 
                    previousNode, 
                    previousNode.isBeginNode() ? ApprovalStateCode.Checking : ApprovalStateCode.Approved, 
                    node.getNodeCode());
                // 只有发起节点可以撤回操作
                if(previousNode.isBeginNode()) {
                    linkState(
                        definitionSetter.get().createFlowState(), 
                        node, 
                        ApprovalStateCode.Revoked, 
                        previousNode.getNodeCode());
                }
            }

            ApprovalNodeSetter<TContainer> next = 
                new ApprovalNodeSetter<>(buildingContext, beginNode, node);
            return next;
        }

        private void linkState(StateMachineFlowState state, StateMachineFlowNode node, String stateCode, String toNodeCode) {
            state.setStateCode(stateCode);
            state.setStateName(buildingContext.getStateNameGetter().call(stateCode));
            state.setToNodeCode(toNodeCode);
            node.addState(state);
        }

    }

    //#endregion
    
}
