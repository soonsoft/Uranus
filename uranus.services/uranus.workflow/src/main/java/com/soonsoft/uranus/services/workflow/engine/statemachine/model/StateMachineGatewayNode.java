package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.Collection;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.services.workflow.exception.FlowException;

/**
 * 闸道器节点
 * 用于处理并行节点与分支节点
 */
public class StateMachineGatewayNode extends StateMachineFlowNode {

    public boolean addState(StateMachineGatewayState state) {
        return super.addState(state);
    }

    @Override
    public void setNodeType(StateMachineFlowNodeType nodeType) {
        if(nodeType == StateMachineFlowNodeType.EndNode) {
            throw new FlowException("the StateMachineGatewayNode can not be end node.");
        }
        super.setNodeType(nodeType);
    }

    public StateMachineFlowState matchState(Object data) {
        for(StateMachineFlowState state : getStateList()) {
            if(state instanceof StateMachineGatewayState gatewayState) {
                if(gatewayState.predicate(data)) {
                    return gatewayState;
                }
            }
        }
        return null;
    }

    public static class StateMachineParallelNode extends StateMachineGatewayNode {
        private List<StateMachinePartialItem> parallelNodeItems;
        private Func1<String, StateMachineFlowNode> findFlowNodeFn;

        public StateMachineParallelNode(Func1<String, StateMachineFlowNode> findNodeFn) {
            this.findFlowNodeFn = findNodeFn;
        }

        public void updateItemStatus(String parallelNodeCode, StateMachinePartialItemStatus status) {
            if(!CollectionUtils.isEmpty(parallelNodeItems)) {
                parallelNodeItems.forEach(i -> {
                    if(i.getItemCode().equals(parallelNodeCode)) {
                        i.setStatus(status);
                    }
                });
            }
        }

        public void updateItemStatus(Collection<String> parallelNodeCodes, StateMachinePartialItemStatus status) {
            if(!CollectionUtils.isEmpty(parallelNodeItems) && !CollectionUtils.isEmpty(parallelNodeCodes)) {
                parallelNodeItems.forEach(i -> {
                    if(parallelNodeCodes.contains(i.getItemCode())) {
                        i.setStatus(status);
                    }
                });
            }
        }

        public StateMachineFlowNode getActionNode(String nodeCode) {
            if(CollectionUtils.isEmpty(parallelNodeItems)) {
                throw new FlowException("there are not parallel node items.");
            }
            if(!parallelNodeItems.stream().anyMatch(i -> i.getItemCode().equals(nodeCode))) {
                throw new FlowException("the nodeCode[%s] not exists.", nodeCode);
            }

            return findFlowNodeFn.call(nodeCode);
        }

        public boolean isCompleted() {
            return !CollectionUtils.isEmpty(parallelNodeItems)
                && parallelNodeItems.stream().allMatch(i -> i.getStatus() == StateMachinePartialItemStatus.Completed);
        }
    }

    public static class StateMachineGatewayState extends StateMachineFlowState {
        private Predicate1<Object> conditionFn;

        public StateMachineGatewayState(Predicate1<Object> conditionFn) {
            this.conditionFn = conditionFn;
        }

        public boolean predicate(Object data) {
            return conditionFn != null ? conditionFn.test(data) : false;
        }
    }
    
}
