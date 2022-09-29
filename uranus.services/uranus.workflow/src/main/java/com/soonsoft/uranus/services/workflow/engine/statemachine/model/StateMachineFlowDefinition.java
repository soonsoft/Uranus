package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public class StateMachineFlowDefinition extends FlowDefinition<StateMachineFlowNode> {

    private String currentNodeCode;

    private String previousStateCode;

    private String previousNodeCode;

    public String getCurrentNodeCode() {
        return currentNodeCode;
    }

    public void setCurrentNodeCode(String currentNodeCode) {
        this.currentNodeCode = currentNodeCode;
    }

    public String getPreviousNodeCode() {
        return previousNodeCode;
    }

    public void setPreviousNodeCode(String previousNodeCode) {
        this.previousNodeCode = previousNodeCode;
    }

    public String getPreviousStateCode() {
        return previousStateCode;
    }

    public void setPreviousStateCode(String previousStateCode) {
        this.previousStateCode = previousStateCode;
    }

    public StateMachineFlowState createFlowState() {
        return new StateMachineFlowState(this::findNode);
    }

    public StateMachineFlowCancelState createCancelState() {
        return new StateMachineFlowCancelState(this::findNode);
    }

    public StateMachineFlowNode createFlowNode() {
        StateMachineFlowNode node = new StateMachineFlowNode();
        node.setFlowCode(getFlowCode());
        return node;
    }

    public StateMachineFlowNode findNode(String nodeCode) {
        List<StateMachineFlowNode> nodeList = getNodeList();
        for(StateMachineFlowNode node : nodeList) {
            if(node.getNodeCode().equals(nodeCode)) {
                return node;
            }
        }
        return null;
    }

    public StateMachineFlowNode finNode(Predicate1<StateMachineFlowNode> predicate) {
        List<StateMachineFlowNode> nodeList = getNodeList();
        for(StateMachineFlowNode node : nodeList) {
            if(predicate.test(node)) {
                return node;
            }
        }
        return null;
    }
}
