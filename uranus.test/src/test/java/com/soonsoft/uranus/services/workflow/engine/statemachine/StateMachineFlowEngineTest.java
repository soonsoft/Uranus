package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.soonsoft.uranus.services.workflow.engine.statemachine.behavior.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItemStatus;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
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

    @Test
    public void test_composition() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("compositionFlow")
                .setFlowName("包含复杂节点的流程")
                .setFlowType("TestComposition")
                .setCancelable(false)
                .beginNode().setNodeCode("begin")
                    .state().setStateCode("Next").setToNodeCode("会签").add()
                    .add()
                .compositeNode(node -> node.resolveState("approved", "denied"))
                    .setNodeCode("会签")
                        .partial().setItemCode("业务领导审批").add()
                        .partial().setItemCode("人事领导审批").add()
                        .partial().setItemCode("分管副总审批").add()
                        .state().setStateCode("approved").setToNodeCode("或签").add()
                        .state().setStateCode("denied").setToNodeCode("begin").add()
                    .add()
                .compositeNode(node -> node.resolveState("denied", "approved"))
                    .setNodeCode("或签")
                        .partial().setItemCode("CEO审批").add()
                        .partial().setItemCode("董事长审批").add()
                        .state().setStateCode("approved").setToNodeCode("end").add()
                        .state().setStateCode("denied").setToNodeCode("begin").add()
                    .add()
                .endNode().setNodeCode("end")
                    .add()
                .build();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        assert engine.getStatus() == FlowStatus.Pending;
        engine.start();
        assert engine.isStarted();

        CompositeActionParameter parameter = new CompositeActionParameter();
        parameter.setOperateTime(new Date());

        parameter.setOperator("制单人");
        engine.action(definition.getCurrentNodeCode(), "Next", parameter);
        assert definition.getCurrentNodeCode().equals("会签");

        // 会签驳回
        resetPartialItems(definition, "会签");
        parameter.setOperator("业务领导");
        parameter.setItemCode("业务领导审批");
        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        assert definition.getCurrentNodeCode().equals("会签");

        parameter.setOperator("人事领导");
        parameter.setItemCode("人事领导审批");
        engine.action(definition.getCurrentNodeCode(), "denied", parameter);
        assert definition.getCurrentNodeCode().equals("begin");

        // 制单人再次提交
        parameter.setOperator("制单人");
        engine.action(definition.getCurrentNodeCode(), "Next", parameter);
        assert definition.getCurrentNodeCode().equals("会签");

        // 会签通过
        resetPartialItems(definition, "会签");
        parameter.setOperator("业务领导");
        parameter.setItemCode("业务领导审批");
        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        parameter.setOperator("人事领导");
        parameter.setItemCode("人事领导审批");
        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        parameter.setOperator("分管副总");
        parameter.setItemCode("分管副总审批");
        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        assert definition.getCurrentNodeCode().equals("或签");

        // 或签通过
        resetPartialItems(definition, "会签");
        parameter.setOperator("CEO");
        parameter.setItemCode("CEO审批");
        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        assert definition.getCurrentNodeCode().equals("end");

        assert engine.isFinished();

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

    private void resetPartialItems(StateMachineFlowDefinition definition, String nodeCode) {
        StateMachineCompositeNode node = 
            (StateMachineCompositeNode) definition.getNodeList()
                .stream()
                .filter(i -> i.getNodeCode().equals(nodeCode))
                .findFirst().get();
        if(node.getPartialItemList() != null) {
            node.getPartialItemList().forEach(i -> {
                i.setStateCode(null);
                i.setStatus(StateMachinePartialItemStatus.Pending);
            });
        }
    }

    public static class CompositeActionParameter extends FlowActionParameter implements IPartialItemCode {

        private String itemCode;

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        @Override
        public String getItemCode() {
            return itemCode;
        }
    }

    //#endregion
    
}
