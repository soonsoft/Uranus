package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate2;
import com.soonsoft.uranus.services.workflow.exception.FlowException;

/**
 * 闸道器节点
 * 用于处理并行节点与分支节点
 */
public abstract class StateMachineGatewayNode extends StateMachineFlowNode {

    @Override
    public void setNodeType(StateMachineFlowNodeType nodeType) {
        if(nodeType == StateMachineFlowNodeType.BeginNode || nodeType == StateMachineFlowNodeType.EndNode) {
            throw new FlowException("the StateMachineGatewayNode cannot be begin node or end node.");
        }
        super.setNodeType(nodeType);
    }

    public StateMachineFlowState matchState(Object data) {
        for(StateMachineFlowState state : getStateList()) {
            if(predicate(state, data)) {
                return state;
            }
        }
        return null;
    }

    protected abstract boolean predicate(StateMachineFlowState state, Object data);

    //#region define node

    public static class StateMachineForkNode extends StateMachineGatewayNode {
        public boolean addState(StateMachineForkState state) {
            return super.addState(state);
        }

        @Override
        protected boolean predicate(StateMachineFlowState state, Object data) {
            if(state instanceof StateMachineForkState gatewayState) {
                return gatewayState.predicate(data, this);
            }
            return false;
        }
    }

    public static class StateMachineParallelNode extends StateMachineGatewayNode implements IPartialItemBehavior {

        private List<StateMachinePartialItem> parallelNodeItems;
        private final transient Func1<String, StateMachineFlowNode> findFlowNodeFn;

        public StateMachineParallelNode(Func1<String, StateMachineFlowNode> findNodeFn) {
            this.findFlowNodeFn = findNodeFn;
        }

        @Override
        public List<StateMachinePartialItem> getPartialItemList() {
            return parallelNodeItems;
        }

        public void setParallelNodeItems(List<StateMachinePartialItem> parallelNodeItems) {
            this.parallelNodeItems = parallelNodeItems;
        }

        public boolean addPartialItem(StateMachinePartialItem partialItem) {
            if(partialItem != null) {
                if(parallelNodeItems == null) {
                    parallelNodeItems = new ArrayList<>();
                }
                return parallelNodeItems.add(partialItem);
            }
            return false;
        }

        public boolean addState(StateMachineParallelState state) {
            return super.addState(state);
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
            return parallelNodeItems != null
                && parallelNodeItems.stream().allMatch(i -> i.getStatus() == StateMachinePartialItemStatus.Completed);
        }

        @Override
        public StateMachineParallelNode copy() {
            StateMachineParallelNode copyNode = new StateMachineParallelNode(this.getFindFlowNodeFn());
            copy(this, copyNode);
            return copyNode;
        }

        @Override
        protected boolean predicate(StateMachineFlowState state, Object data) {
            if(state instanceof StateMachineParallelState parallelState) {
                return parallelState.predicate(data, this);
            }
            return false;
        }

        @Transient
        protected Func1<String, StateMachineFlowNode> getFindFlowNodeFn() {
            return findFlowNodeFn;
        }

        public static void copy(StateMachineParallelNode source, StateMachineParallelNode dist) {
            StateMachineFlowNode.copy(source, dist);
            if(source.getPartialItemList() != null) {
                for(StateMachinePartialItem partialItem : source.getPartialItemList()) {
                    dist.addPartialItem(partialItem.copy());
                }
            }
        }
    }

    //#endregion

    //#region define state

    public static abstract class StateMachineGatewayState<TNode> extends StateMachineFlowState {
        private final transient Predicate2<Object, TNode> conditionFn;

        public StateMachineGatewayState(Predicate2<Object, TNode> conditionFn) {
            this.conditionFn = conditionFn;
        }

        public boolean predicate(Object data, TNode node) {
            return conditionFn != null ? conditionFn.test(data, node) : false;
        }

        @Transient
        protected Predicate2<Object, TNode> getConditionFn() {
            return conditionFn;
        }

    }

    public static class StateMachineForkState extends StateMachineGatewayState<StateMachineForkNode> {
        public final transient String DEFAULT_STATE_CODE = "@ForkNodeAutoFlow";

        public StateMachineForkState(Predicate2<Object, StateMachineForkNode> conditionFn) {
            super(conditionFn);
            setStateCode(DEFAULT_STATE_CODE);
        }

        @Override
        public StateMachineForkState copy() {
            StateMachineForkState copyState = new StateMachineForkState(this.getConditionFn());
            copy(this, copyState);
            return copyState;
        }

        public static void copy(StateMachineForkState source, StateMachineForkState dist) {
            StateMachineFlowState.copy(source, dist);
        }

    }

    public static class StateMachineParallelState extends StateMachineGatewayState<StateMachineParallelNode> {
        public final transient String DEFAULT_STATE_CODE = "@ParallelNodeToNext";

        public StateMachineParallelState(Predicate2<Object, StateMachineParallelNode> conditionFn) {
            super(conditionFn);
            setStateCode(DEFAULT_STATE_CODE);
        }

        @Override
        public StateMachineParallelState copy() {
            StateMachineParallelState copyState = new StateMachineParallelState(this.getConditionFn());
            copy(this, copyState);
            return copyState;
        }

        public static void copy(StateMachineParallelState source, StateMachineParallelState dist) {
            if(source == null || dist == null) {
                return;
            }
            StateMachineFlowState.copy(source, dist);
        }

    }

    //#endregion
    
}
