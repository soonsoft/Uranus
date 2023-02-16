package com.soonsoft.uranus.services.workflow.engine.linear;

import java.util.UUID;

import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowNode;
import com.soonsoft.uranus.services.workflow.engine.linear.model.LinearFlowState;
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

        definition.getNodeList().forEach(n -> {
            LinearFlowState state = new LinearFlowState();
            state.setStateCode("Next");
            n.addState(state);
            n.setNodeCode(UUID.randomUUID().toString());
        });

        LinearFlowEngine<Object> engine = new LinearFlowEngine<>(definition);
        engine.setFlowRepository(new LinearFlowRepository());
        engine.getFlowStatusChangedEvent().on(event -> {
            System.out.println(String.format("%s > %s", event.getPreviousStatus(), event.getData().getStatus()));
        });
        engine.getFlowActionEvent().on(event -> {
            System.out.println(String.format("%s.%s",
                    event.getData().getNodeCode(),
                    event.getData().getStateCode()));
        });
        engine.start();

        assert definition.getNodeList().get(0).getStepValue() == 1;
        assert definition.getNodeList().get(definition.getNodeList().size() - 1).getStepValue() == 100;
        definition.getNodeList().forEach(n -> {
            if (n.getStepValue() == 1) {
                assert n.getNodeStatus() == LinearFlowStatus.Activated;
            }
        });

        definition.getNodeList().forEach(n -> {
            engine.action(n.getNodeCode(), "Next");
        });
    }

}
