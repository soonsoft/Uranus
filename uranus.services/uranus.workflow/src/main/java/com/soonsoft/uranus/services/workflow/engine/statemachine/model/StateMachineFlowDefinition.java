package com.soonsoft.uranus.services.workflow.engine.statemachine.model;

import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.core.functional.predicate.Predicate2;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineForkNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineForkState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelState;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public class StateMachineFlowDefinition extends FlowDefinition<StateMachineFlowNode> implements ICopy<StateMachineFlowDefinition> {

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

    //#region state creator

    public StateMachineFlowState createFlowState() {
        StateMachineFlowState state = new StateMachineFlowState(this::findNode);
        state.setFlowCode(getFlowCode());
        return state;
    }

    public StateMachineFlowCancelState createCancelState() {
        StateMachineFlowCancelState state = new StateMachineFlowCancelState(this::findNode);
        state.setFlowCode(getFlowCode());
        return state;
    }

    public StateMachineForkState createForkState(Predicate2<Object, StateMachineForkNode> predicate) {
        StateMachineForkState state = new StateMachineForkState(predicate);
        state.setFlowCode(getFlowCode());
        state.setFindFlowNodeFn(this::findNode);
        return state;
    }

    public StateMachineParallelState createParallelState(Predicate2<Object, StateMachineParallelNode> predicate) {
        StateMachineParallelState state = new StateMachineParallelState(predicate);
        state.setFlowCode(getFlowCode());
        state.setFindFlowNodeFn(this::findNode);
        return state;
    }

    //#endregion

    //#region node creator

    public StateMachineFlowNode createFlowNode() {
        StateMachineFlowNode node = new StateMachineFlowNode();
        node.setFlowCode(getFlowCode());
        return node;
    }

    public StateMachineCompositeNode createCompositeNode(Func1<StateMachineCompositeNode, String> resolveStateCodeFn) {
        StateMachineCompositeNode node = new StateMachineCompositeNode(resolveStateCodeFn);
        node.setFlowCode(getFlowCode());
        return node;
    }

    public StateMachineForkNode createForkNode() {
        StateMachineForkNode node = new StateMachineForkNode();
        node.setFlowCode(getFlowCode());
        return node;
    }

    public StateMachineParallelNode createParallelNode() {
        StateMachineParallelNode node = new StateMachineParallelNode(this::findNode);
        node.setFlowCode(getFlowCode());
        return node;
    }

    //#endregion

    public StateMachineFlowNode getBeginNode() {
        return findNode(n -> n.getNodeType() == StateMachineFlowNodeType.BeginNode);
    }

    public StateMachineFlowNode getEndNode() {
        return findNode(n -> n.getNodeType() == StateMachineFlowNodeType.EndNode);
    }

    public StateMachineFlowNode findNode(String nodeCode) {
        List<StateMachineFlowNode> nodeList = getNodeList();
        if(!CollectionUtils.isEmpty(nodeList)) {
            for(StateMachineFlowNode node : nodeList) {
                if(node.getNodeCode().equals(nodeCode)) {
                    return node;
                }
            }
        }
        return null;
    }

    public StateMachineFlowNode findNode(Predicate1<StateMachineFlowNode> predicate) {
        List<StateMachineFlowNode> nodeList = getNodeList();
        if(!CollectionUtils.isEmpty(nodeList)) {
            for(StateMachineFlowNode node : nodeList) {
                if(predicate.test(node)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    public StateMachineFlowDefinition copy() {
        StateMachineFlowDefinition copyDefinition = new StateMachineFlowDefinition();
        copy(this, copyDefinition);
        return copyDefinition;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public static void copy(StateMachineFlowDefinition source, StateMachineFlowDefinition dist) {
        if(source == null || dist == null) {
            return;
        }
        dist.setId(source.getId());
        dist.setFlowCode(source.getFlowCode());
        dist.setFlowName(source.getFlowName());
        dist.setFlowType(source.getFlowType());
        dist.setStatus(source.getStatus());
        dist.setDescription(source.getDescription());
        dist.setPreviousNodeCode(source.getPreviousNodeCode());
        dist.setPreviousStateCode(source.getPreviousStateCode());
        dist.setCurrentNodeCode(source.getCurrentNodeCode());

        if(source.getNodeList() != null) {
            for(StateMachineFlowNode node : source.getNodeList()) {
                StateMachineFlowNode copyNode = node.copy();
                if(copyNode.getStateList() != null) {
                    for(StateMachineFlowState state : copyNode.getStateList()) {
                        state.setFindFlowNodeFn(dist::findNode);
                    }
                }
                dist.addNode(copyNode);
            }
        }
    }
}
