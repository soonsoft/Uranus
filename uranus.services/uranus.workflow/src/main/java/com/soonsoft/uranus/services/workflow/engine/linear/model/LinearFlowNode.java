package com.soonsoft.uranus.services.workflow.engine.linear.model;

import java.util.List;

import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.services.workflow.model.FlowNode;

public class LinearFlowNode extends FlowNode<LinearFlowState> {

    private int stepValue;
    private String actionStateCode;
    private LinearFlowStatus nodeStatus = LinearFlowStatus.Pending;
    private Func1<Predicate1<LinearFlowNode>, List<LinearFlowNode>> findNodeListFn;

    public LinearFlowNode() {

    }

    public LinearFlowNode(Func1<Predicate1<LinearFlowNode>, List<LinearFlowNode>> findNodeListFn) {
        this.findNodeListFn = findNodeListFn;
    }

    public int getStepValue() {
        return stepValue;
    }

    public void setStepValue(int stepValue) {
        this.stepValue = stepValue;
    }

    public String getActionStateCode() {
        return actionStateCode;
    }

    public void setActionStateCode(String actionStateCode) {
        this.actionStateCode = actionStateCode;
    }

    public LinearFlowStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(LinearFlowStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public void setFindNodeListFn(Func1<Predicate1<LinearFlowNode>, List<LinearFlowNode>> findNodeListFn) {
        this.findNodeListFn = findNodeListFn;
    }

    public List<LinearFlowNode> getSameStepNodeList() {
        if(findNodeListFn != null) {
            final LinearFlowNode selfNode = this;
            final int step = stepValue;
            return findNodeListFn.call(n -> n != selfNode && n.getStepValue() == step);
        }
        return null;
    }

    @Override
    public void setStateList(List<LinearFlowState> stateList) {
        if(!CollectionUtils.isEmpty(stateList)) {
            stateList.forEach(s -> s.setNodeCode(getNodeCode()));
        }
        super.setStateList(stateList);
    }

    @Override
    public boolean addState(LinearFlowState state) {
        if(state != null) {
            state.setNodeCode(getNodeCode());
            return super.addState(state);
        }
        return false;
    }
    
}
