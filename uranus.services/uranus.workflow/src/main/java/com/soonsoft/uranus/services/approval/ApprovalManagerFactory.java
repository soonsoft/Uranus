package com.soonsoft.uranus.services.approval;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.approval.exception.ApprovalException;
import com.soonsoft.uranus.services.approval.model.ApprovalStateCode;
import com.soonsoft.uranus.services.approval.simple.SimpleApprovalManager;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory.StateMachineFlowDefinitionSetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.StateMachineFlowFactory.StateMachineFlowNodeSetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;

public class ApprovalManagerFactory<TApprovalQuery> {

    public IApprovalManager<TApprovalQuery> createManager(
            TApprovalQuery query, 
            IApprovalRepository approvalRepository,
            ApprovalDefinitionContainer definitionContainer) {

        return createManager(query, approvalRepository, () ->  UUID.randomUUID().toString(), definitionContainer);

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
        return new ApprovalDefinitionContainer();
    }

    public static abstract class BaseDefinitionContainer<T extends BaseDefinitionContainer<T>> {
        private Map<String, StateMachineFlowDefinition> map = new HashMap<>();

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

        public StateMachineFlowDefinition get(String approvalType) {
            return map.get(approvalType);
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }
    }

    public static class ApprovalDefinitionContainer extends BaseDefinitionContainer<ApprovalDefinitionContainer> {
        
        public ApprovalDefinitionBuilder<ApprovalDefinitionContainer> definition(String approvalType, String flowCode, String flowName, boolean cancelable) {
            StateMachineFlowDefinitionSetter flowDefinitionSetter = 
                StateMachineFlowFactory.builder()
                    .setFlowCode(flowCode)
                    .setFlowName(flowName)
                    .setCancelable(cancelable)
                    .setFlowType(approvalType);
            ApprovalDefinitionBuilder<ApprovalDefinitionContainer> definitionBuilder = 
                new ApprovalDefinitionBuilder<>(this, flowDefinitionSetter);
            return definitionBuilder;
        }

    }

    public static class ApprovalDefinitionBuilder<TContainer> {

        private final TContainer parent;
        private final StateMachineFlowDefinitionSetter flowDefinitionSetter;

        public ApprovalDefinitionBuilder(TContainer parent, StateMachineFlowDefinitionSetter flowDefinitionSetter) {
            this.parent = parent;
            this.flowDefinitionSetter = flowDefinitionSetter;
        }

        public ApprovalNodeSetter<TContainer> begin() {
            return begin("ApprovalBeginNode", "Begin");
        }

        public ApprovalNodeSetter<TContainer> begin(String beginNodeCode, String beginNodeName) {
            StateMachineFlowNodeSetter nodeSetter = 
                flowDefinitionSetter.beginNode()
                    .setNodeCode(beginNodeCode)
                    .setNodeName(beginNodeName);
            
            StateMachineFlowNode beginNode = nodeSetter.get();
            return new ApprovalNodeSetter<TContainer>(parent, beginNode, beginNode, () -> flowDefinitionSetter);
        }

    }

    public static class ApprovalNodeSetter<TContainer> {

        private final TContainer container;
        private final StateMachineFlowNode beginNode;
        private final StateMachineFlowNode previousNode;
        private final Func0<StateMachineFlowDefinitionSetter> definitionSetterFactory;

        public ApprovalNodeSetter(
                TContainer container, 
                StateMachineFlowNode beginNode,
                StateMachineFlowNode previousNode, 
                Func0<StateMachineFlowDefinitionSetter> definitionSetterFactory) {
            this.container = container;
            this.beginNode = beginNode;
            this.previousNode = previousNode;
            this.definitionSetterFactory = definitionSetterFactory;
        }

        public ApprovalNodeSetter<TContainer> next(String nodeCode, String nodeName) {
            StateMachineFlowDefinitionSetter definitionSetter = definitionSetterFactory.call();
            StateMachineFlowNodeSetter nodeSetter = 
                definitionSetter.node()
                    .setNodeCode(nodeCode)
                    .setNodeName(nodeName)
                    .state()
                        .setStateCode(ApprovalStateCode.Denied)
                        .setStateName(ApprovalStateCode.Denied)
                        .setToNodeCode(beginNode.getNodeCode())
                        .add();
            StateMachineFlowNode node = nodeSetter.get();
            nodeSetter.add();

            if(previousNode != null) {
                linkState(
                    definitionSetter.get().createFlowState(), 
                    previousNode, 
                    ApprovalStateCode.Approved, 
                    node.getNodeCode());
            }

            ApprovalNodeSetter<TContainer> next = 
                new ApprovalNodeSetter<>(container, beginNode, node, definitionSetterFactory);
            return next;
        }

        public TContainer end() {
            StateMachineFlowDefinitionSetter definitionSetter = definitionSetterFactory.call();
            String endNodeCode = "ApprovalEndNode";
            definitionSetter
                .endNode()
                    .setNodeCode(endNodeCode)
                    .setNodeName("End")
                    .add();
                
            if(previousNode != null) {
                linkState(
                    definitionSetter.get().createFlowState(), 
                    previousNode, 
                    ApprovalStateCode.Approved, 
                    endNodeCode);
            }
            
            return container;
        }

        private void linkState(StateMachineFlowState state, StateMachineFlowNode node, String stateCode, String toNodeCode) {
            if(previousNode != null) {
                state.setStateCode(stateCode);
                if(state.getStateName() != null) {
                    state.setStateName(stateCode);
                }
                state.setToNodeCode(toNodeCode);
                previousNode.addState(state);
            }
        }

    }
    
}
