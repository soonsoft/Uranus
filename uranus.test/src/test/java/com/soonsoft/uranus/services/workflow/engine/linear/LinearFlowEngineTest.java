package com.soonsoft.uranus.services.workflow.engine.linear;

import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowNode;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowStatus;

public class LinearFlowEngineTest {
    
    @Test
    public void test_sortNodeList() {
        LinearFlowDefinition definition = new LinearFlowDefinition();

        LinearFlowNode node = new LinearFlowNode();
        node.setStepValue(55);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(8);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(1);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(1);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(100);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(8);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(20);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(2);
        definition.addNode(node);

        node = new LinearFlowNode();
        node.setStepValue(8);
        definition.addNode(node);

        LinearFlowEngine<Object> engine = new LinearFlowEngine<>(definition);
        engine.setFlowRepository(new LinearFlowRepository());
        engine.start();

        assert definition.getNodeList().get(0).getStepValue() == 1;
        assert definition.getNodeList().get(definition.getNodeList().size() - 1).getStepValue() == 100;
        definition.getNodeList().forEach(n -> {
            if(n.getStepValue() == 1) {
                assert n.getNodeStatus() == LinearFlowStatus.Activated;
            }
        });
    }

}
