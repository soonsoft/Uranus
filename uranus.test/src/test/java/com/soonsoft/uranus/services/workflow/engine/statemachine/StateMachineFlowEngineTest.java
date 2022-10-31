package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public class StateMachineFlowEngineTest {

    private StateMachineFlowFactory<StateMachineFlowDataQuery> factory;

    @Before
    public void initial() {
        factory = createFactory();
    }

    @Test
    public void test_query() {
        StateMachineFlowDefinition definition = createDefaultTestDefinition();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);
        List<String> codes = engine.query().queryAllCodes();
        Assert.assertTrue(codes.size() == 2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_extensionQuery() {
        Object factoryObj = factory;
        StateMachineFlowFactory<DataSubQuery> factory2 = (StateMachineFlowFactory<DataSubQuery>) factoryObj;
    
        StateMachineFLowEngine<DataSubQuery> engine = factory2.createEngine(createDefaultTestDefinition());
        Object data = engine.query().getData("type", 1);
        assert data.toString() == "{}"; 
    }

    @Test
    public void test_flow() {
        StateMachineFlowDefinition definition = createDefaultTestDefinition();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        assert engine.getStatus() == FlowStatus.Pending;

        engine.start();
        assert definition.getCurrentNodeCode().equals("001");
        assert engine.isStarted();

        engine.action("001", "Next");
        assert definition.getCurrentNodeCode().equals("002");

        engine.action("002", "Next");
        assert definition.getCurrentNodeCode().equals("003");

        // 回退
        engine.action("003", "Previous");
        assert definition.getCurrentNodeCode().equals("002");

        engine.action("002", "Next");
        assert definition.getCurrentNodeCode().equals("003");

        // 继续
        engine.action("003", "Next");
        assert definition.getCurrentNodeCode().equals("004");

        engine.action("004", "BackToFirst");
        assert definition.getCurrentNodeCode().equals("001");

        // 从头再来
        engine.action("001", "Next");
        assert definition.getCurrentNodeCode().equals("002");

        engine.action("002", "Next");
        assert definition.getCurrentNodeCode().equals("003");

        engine.action("003", "Next");
        assert definition.getCurrentNodeCode().equals("004");

        engine.action("004", "Next");
        assert definition.getCurrentNodeCode().equals("005");
        assert engine.isFinished();
    }

    @Test
    public void test_flowAction() {
        StateMachineFlowRepository repository = (StateMachineFlowRepository) factory.getRepository();
        repository.setDefinitionFn(p -> createDefaultTestDefinition());
        repository.setCurrentStateFn(p -> {
            StateMachineFlowState currentState = new StateMachineFlowState();
            currentState.setFlowCode("002");
            currentState.setStateCode("Next");
            currentState.setStateName("Next");
            currentState.setToNodeCode("003");
            return currentState;
        });

        Object parameter = null;
        StateMachineFlowDefinition definition = factory.loadDefinition(parameter);
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        engine.action("003", "Next");
        assert definition.getCurrentNodeCode().equals("004");
    }

    @Test
    public void test_flowCancel() {
        StateMachineFlowDefinition definition = createDefaultTestDefinition();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        assert engine.getStatus() == FlowStatus.Pending;

        engine.start();
        assert engine.isStarted();

        engine.cancel(null);
        assert engine.isCanceled();
    }

    //#region helper methonds

    private StateMachineFlowFactory<StateMachineFlowDataQuery> createFactory() {
        StateMachineFlowRepository repository = new StateMachineFlowRepository();
        repository.setDefinitionFn(p -> createDefaultTestDefinition());

        DataSubQuery query = new DataSubQuery();
        StateMachineFlowFactory<StateMachineFlowDataQuery> factory = new StateMachineFlowFactory<>(repository, query);
        return factory;
    }

    private StateMachineFlowDefinition createDefaultTestDefinition() {
        StateMachineFlowDefinition definition = new StateMachineFlowDefinition();
        definition.setFlowCode("Test_StateMachine_Def");
        definition.setFlowName("Test State Machine Definition");
        definition.setCancelable(true);

        StateMachineFlowNode node = definition.createFlowNode();
        node.setNodeCode("001");
        node.setNodeName("BeginNode");
        node.setNodeType(StateMachineFlowNodeType.BeginNode);
        addState(definition, node, "Next", "Next", "002");
        definition.addNode(node);

        node = definition.createFlowNode();
        node.setNodeCode("002");
        node.setNodeName("Node002");
        node.setNodeType(StateMachineFlowNodeType.NormalNode);
        addState(definition, node, "Next", "Next", "003");
        addState(definition, node, "Previous", "Previous", "001");
        definition.addNode(node);

        node = definition.createFlowNode();
        node.setNodeCode("003");
        node.setNodeName("Node003");
        node.setNodeType(StateMachineFlowNodeType.NormalNode);
        addState(definition, node, "Next", "Next", "004");
        addState(definition, node, "Previous", "Previous", "002");
        definition.addNode(node);

        node = definition.createFlowNode();
        node.setNodeCode("004");
        node.setNodeName("Node004");
        node.setNodeType(StateMachineFlowNodeType.NormalNode);
        addState(definition, node, "Next", "Next", "005");
        addState(definition, node, "BackToFirst", "BackToFirst", "001");
        definition.addNode(node);

        node = definition.createFlowNode();
        node.setNodeCode("005");
        node.setNodeName("EndNode");
        node.setNodeType(StateMachineFlowNodeType.EndNode);
        definition.addNode(node);

        return definition;
    }

    private void addState(StateMachineFlowDefinition definition, StateMachineFlowNode node, String stateCode, String stateName, String toNodeCode) {
        StateMachineFlowState state = definition.createFlowState();
        state.setFlowCode(definition.getFlowCode());
        state.setStateCode(stateCode);
        state.setStateName(stateName);
        state.setToNodeCode(toNodeCode);
        node.addState(state);
    }

    //#endregion
    
}
