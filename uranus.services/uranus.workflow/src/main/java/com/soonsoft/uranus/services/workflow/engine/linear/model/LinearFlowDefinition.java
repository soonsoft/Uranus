package com.soonsoft.uranus.services.workflow.engine.linear.model;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;
import com.soonsoft.uranus.services.workflow.model.FlowDefinition;

public class LinearFlowDefinition extends FlowDefinition<LinearFlowNode> {

    public List<LinearFlowNode> findNode(final int stepValue) {
        return findNode(n -> n.getStepValue() == stepValue);
    }

    public List<LinearFlowNode> findNode(Predicate1<LinearFlowNode> filter) {
        Guard.notNull(filter, "the parameter filter is required.");

        List<LinearFlowNode> resultNodeList = new ArrayList<>();
        for(LinearFlowNode node : this.getNodeList()) {
            if(filter.test(node)) {
                resultNodeList.add(node);
            }
        }
        return resultNodeList;
    }

    public LinearFlowNode createFlowNode() {
        return new LinearFlowNode(this::findNode);
    }
    
}
