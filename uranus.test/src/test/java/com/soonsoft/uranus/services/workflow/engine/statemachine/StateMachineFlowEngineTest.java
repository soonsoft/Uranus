package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
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

        // SerializedLambda sl = new SerializedLambda(getClass(), null, null, null, 0, null, null, null, null, null);
        // LambdaMetafactory.

        DataSubQuery query = new DataSubQuery();
        StateMachineFlowFactory<StateMachineFlowDataQuery> factory = new StateMachineFlowFactory<>(repository, query);
        return factory;
    }

    private StateMachineFlowDefinition createDefaultTestDefinition() {
        final String nextState = "Next";
        final String nextStateName = "下一个";
        final String previousState = "Previous";
        final String previousStateName = "上一个";
        
        return factory.definitionBuilder()
            .setFlowCode("Test_StateMachine_Def")
            .setFlowName("Test State Machine Definition")
            .setFlowType("Test")
            .setCancelable(true)
            .node()
                .setNodeCode("001")
                .setNodeName("BeginNode")
                .setNodeType(StateMachineFlowNodeType.BeginNode)
                .state()
                    .setStateCode(nextState)
                    .setStateName(nextStateName)
                    .setToNodeCode("002")
                    .add()
                .add()
            .node()
                .setNodeCode("002")
                .setNodeName("Node002")
                .setNodeType(StateMachineFlowNodeType.NormalNode)
                .state()
                    .setStateCode(nextState)
                    .setStateName(nextStateName)
                    .setToNodeCode("003")
                    .add()
                .state()
                    .setStateCode(previousState)
                    .setStateName(previousStateName)
                    .setToNodeCode("001")
                    .add()
                .add()
            .node()
                .setNodeCode("003")
                .setNodeName("Node003")
                .setNodeType(StateMachineFlowNodeType.NormalNode)
                .state()
                    .setStateCode(nextState)
                    .setStateName(nextStateName)
                    .setToNodeCode("004")
                    .add()
                .state()
                    .setStateCode(previousState)
                    .setStateName(previousStateName)
                    .setToNodeCode("002")
                    .add()
                .add()
            .node()
                .setNodeCode("004")
                .setNodeName("Node004")
                .setNodeType(StateMachineFlowNodeType.NormalNode)
                .state()
                    .setStateCode(nextState)
                    .setStateName(nextStateName)
                    .setToNodeCode("005")
                    .add()
                .state()
                    .setStateCode(previousState)
                    .setStateName(previousStateName)
                    .setToNodeCode("003")
                    .add()
                .state()
                    .setStateCode("BackToFirst")
                    .setStateName("回到Node001结点")
                    .setToNodeCode("001")
                    .add()
                .add()
            .node()
                .setNodeCode("005")
                .setNodeName("Node005")
                .setNodeType(StateMachineFlowNodeType.EndNode)
                .add()
            .build();
    }

    //#endregion
    
}
