package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class StateMachineFlowDefinition extends FlowDefinition<FlowNode<StateMachineFlowState>> {

    public StateMachineFlowState createFlowState() {
        return new StateMachineFlowState(this);
    }

    public StateMachineFlowCancelState createCancelState() {
        return new StateMachineFlowCancelState(this);
    }

    public FlowNode<StateMachineFlowState> createFlowNode() {
        FlowNode<StateMachineFlowState> node = new FlowNode<>();
        node.setFlowCode(getFlowCode());
        return node;
    }

    public FlowNode<StateMachineFlowState> findNode(String nodeCode) {
        List<FlowNode<StateMachineFlowState>> nodeList = getNodeList();
        for(FlowNode<StateMachineFlowState> node : nodeList) {
            if(node.getNodeCode().equals(nodeCode)) {
                return node;
            }
        }
        return null;
    }

    public FlowNode<StateMachineFlowState> finNode(Predicate1<FlowNode<StateMachineFlowState>> predicate) {
        List<FlowNode<StateMachineFlowState>> nodeList = getNodeList();
        for(FlowNode<StateMachineFlowState> node : nodeList) {
            if(predicate.test(node)) {
                return node;
            }
        }
        return null;
    }
}
