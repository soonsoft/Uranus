package com.soonsoft.uranus.services.workflow.engine.statemachine;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.services.workflow.IFlowDataGetter;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.CompositionPartialState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.IPartialItemCode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.ParallelActionNodeState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineCompositeNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowDefinition;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNode;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowNodeType;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineFlowState;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItem;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachinePartialItemStatus;
import com.soonsoft.uranus.services.workflow.engine.statemachine.model.StateMachineGatewayNode.StateMachineParallelNode;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;
import com.soonsoft.uranus.services.workflow.model.FlowStatus;

public class StateMachineFlowEngineTest {

    private StateMachineFlowFactory<StateMachineFlowDataQuery> factory;

    @Before
    public void initial() {
        factory = createFactory();
    }

    //#region 查询对象

    @Test
    @DisplayName("测试查询结构")
    public void test_query() {
        StateMachineFlowDefinition definition = createDefaultTestDefinition();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);
        List<String> codes = engine.query().queryAllCodes();
        Assert.assertTrue(codes.size() == 2);
    }

    @Test
    @DisplayName("测试扩展查询结构")
    @SuppressWarnings("unchecked")
    public void test_extensionQuery() {
        Object factoryObj = factory;
        StateMachineFlowFactory<DataSubQuery> factory2 = (StateMachineFlowFactory<DataSubQuery>) factoryObj;
    
        StateMachineFLowEngine<DataSubQuery> engine = factory2.createEngine(createDefaultTestDefinition());
        Object data = engine.query().getData("type", 1);
        assert data.toString() == "{}"; 
    }

    //#endregion

    //#region 正向流程测试

    @Test
    @DisplayName("测试基础流程")
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
    @DisplayName("测试流程驱动")
    public void test_flowAction() {
        StateMachineFlowRepository repository = (StateMachineFlowRepository) factory.getRepository();
        repository.setDefinitionFn(p -> createDefaultTestDefinition());
        repository.setCurrentStateFn(p -> {
            StateMachineFlowState currentState = new StateMachineFlowState();
            currentState.setFlowCode("Test_StateMachine_Def");
            currentState.setNodeCode("002");
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
    @DisplayName("复合节点流程")
    public void test_composition() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("复杂节点流程")
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

        TestActionParameter parameter = new TestActionParameter();
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

    @Test
    public void test_fork() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("分支节点流程")
                .setCancelable(false)
                .beginNode().setNodeCode("begin")
                    .state().setStateCode("submit").setToNodeCode("分支节点").add()
                    .add()
                .node().setNodeCode("总经理审批")
                    .state().setStateCode("approved").setToNodeCode("end").add()
                    .state().setStateCode("deiend").setToNodeCode("begin").add()
                    .add()
                .node().setNodeCode("采购经理审批")
                    .state().setStateCode("approved").setToNodeCode("总经理审批").add()
                    .state().setStateCode("deiend").setToNodeCode("begin").add()
                    .add()
                .forkNode().setNodeCode("分支节点")
                    .state((data, forkNode) -> ((Integer)data).intValue() >= 1000).setToNodeCode("总经理审批").add()
                    .state((data, forkNode) -> ((Integer)data).intValue() < 1000).setToNodeCode("采购经理审批").add()
                    .add()
                .endNode().setNodeCode("end").add()
                .build();

        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);
        StringBuilder traceText = new StringBuilder("\n\n[Trace]:\n");
        Action1<StateMachineFlowDefinition> trace = 
            d -> {
                traceText.append(
                    StringUtils.format(
                        "{0}.{1} > {2}\n", 
                        d.getPreviousNodeCode() != null ? d.getPreviousNodeCode() : "~",
                        d.getPreviousStateCode() != null ? d.getPreviousStateCode() : "~",
                        d.getCurrentNodeCode()
                    )
                );
            };

        engine.start();
        assert engine.isStarted();
        trace.apply(definition);

        TestActionParameter parameter = new TestActionParameter();
        parameter.setOperateTime(new Date());

        parameter.setData(Integer.valueOf(500));
        engine.action("begin", "submit", parameter);
        assert definition.getCurrentNodeCode().equals("采购经理审批");
        trace.apply(definition);

        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        assert definition.getCurrentNodeCode().equals("总经理审批");
        trace.apply(definition);

        engine.action(definition.getCurrentNodeCode(), "deiend", parameter);
        assert definition.getCurrentNodeCode().equals("begin");
        trace.apply(definition);

        parameter.setData(Integer.valueOf(1000));
        engine.action("begin", "submit", parameter);
        assert definition.getCurrentNodeCode().equals("总经理审批");
        trace.apply(definition);

        engine.action(definition.getCurrentNodeCode(), "approved", parameter);
        assert definition.getCurrentNodeCode().equals("end");
        assert engine.isFinished();
        trace.apply(definition);

        System.out.println(traceText);
    }

    @Test
    public void test_parallel() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("并行节点流程")
                .setFlowName("软件需求评审")
                .beginNode().setNodeCode("begin")
                    .state().setStateCode("提交").setToNodeCode("评审").add()
                    .add()
                .parallelNode().setNodeCode("评审")
                    .partialNode()
                        .setItemCode("架构师评审")
                        .addState("approved", "同意")
                        .addState("deined", "拒绝")
                        .add()
                    .partialNode()
                        .setItemCode("开发经理评审")
                        .addState("approved", "同意")
                        .addState("deined", "拒绝")
                        .add()
                    .partialNode()
                        .setItemCode("测试经理评审")
                        .addState("approved", "同意")
                        .addState("deined", "拒绝")
                        .add()
                    .state((data, parallelNode) -> parallelNode.allMatch(i -> "approved".equals(i.getStateCode())) && ((Integer)data).intValue() < 200)
                        .setToNodeCode("常规审批").add()
                    .state((data, parallelNode) -> parallelNode.allMatch(i -> "approved".equals(i.getStateCode())) && ((Integer)data).intValue() > 200)
                        .setToNodeCode("CTO审批").add()
                    .add()
                .compositeNode(node -> node.resolveState("approved", "denied"))
                    .setNodeCode("常规审批")
                    .partial().setItemCode("部门经理审批").add()
                    .partial().setItemCode("项目管理部审批").add()
                    .state().setStateCode("approved").setToNodeCode("end").add()
                    .state().setStateCode("denied").setToNodeCode("begin").add()
                    .add()
                .node().setNodeCode("CTO审批")
                    .state().setStateCode("approved").setToNodeCode("end").add()
                    .state().setStateCode("deined").setToNodeCode("begin").add()
                    .add()
                .endNode().setNodeCode("end")
                    .add()
                .build();

        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        engine.start();
        assert engine.isStarted();

        TestActionParameter parameter = new TestActionParameter();
        parameter.setOperateTime(new Date());

        parameter.setOperator("产品经理");
        parameter.setData(Integer.valueOf(100)); // 估时，100 人/月
        engine.action("begin", "提交", parameter);
        assert definition.getCurrentNodeCode().equals("评审");

        parameter.setOperator("架构师");
        engine.action("架构师评审", "approved", parameter);
        parameter.setOperator("开发经理");
        engine.action("开发经理评审", "approved", parameter);
        parameter.setOperator("测试经理");
        engine.action("测试经理评审", "approved", parameter);
        assert definition.getCurrentNodeCode().equals("常规审批");

        parameter.setOperator("部门经理");
        parameter.setItemCode("部门经理审批");
        engine.action("常规审批", "approved", parameter);
        parameter.setOperator("项目管理人员");
        parameter.setItemCode("项目管理部审批");
        engine.action("常规审批", "denied", parameter);
        assert definition.getCurrentNodeCode().equals("begin");

        resetPartialItems(definition, "评审");
        resetPartialItems(definition, "常规审批");

        parameter.setOperator("产品经理");
        parameter.setData(Integer.valueOf(300)); // 修改估时，300 人/月
        engine.action(definition.getCurrentNodeCode(), "提交", parameter);
        assert definition.getCurrentNodeCode().equals("评审");

        parameter.setOperator("架构师");
        engine.action("架构师评审", "approved", parameter);
        parameter.setOperator("开发经理");
        engine.action("开发经理评审", "approved", parameter);
        parameter.setOperator("测试经理");
        engine.action("测试经理评审", "approved", parameter);
        assert definition.getCurrentNodeCode().equals("CTO审批");

        parameter.setOperator("CTO");
        engine.action("CTO审批", "approved", parameter);
        assert definition.getCurrentNodeCode().equals("end");

        assert engine.isFinished();
    }

    @Test
    @DisplayName("测试多重路由节点")
    public void test_gatewayNode() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("多重路由节点流程")
                .setCancelable(false)
                .beginNode().setNodeCode("begin")
                    .state().setStateCode("submit").setToNodeCode("分支节点").add()
                    .add()
                .forkNode().setNodeCode("分支节点")
                    .state((data, forkNode) -> data != null).setToNodeCode("并行节点").add()
                    .state((data, forkNode) -> data == null).setToNodeCode("begin").setStateCode("error").add()
                    .add()
                .parallelNode().setNodeCode("并行节点")
                    .partialNode()
                        .setItemCode("复核").addState("通过", "复合通过").add()
                    .partialNode()
                        .setItemCode("预算").addState("完成", "完成预算").add()
                    .state((data, parallelNode) -> parallelNode.allMatch(i -> i.getStateCode().equals("完成") || i.getStateCode().equals("通过")))
                        .setToNodeCode("自动完成").add()
                    .add()
                .forkNode().setNodeCode("自动完成")
                    .state((data, forkNode) -> ((Integer)data).intValue() >= 1000).setToNodeCode("总经理审批").add()
                    .state((data, forkNode) -> ((Integer)data).intValue() < 1000).setToNodeCode("end").add()
                    .add()
                .node().setNodeCode("总经理审批")
                    .state().setStateCode("通过").setToNodeCode("end").add()
                    .state().setStateCode("拒绝").setToNodeCode("begin").add()
                    .add()
                .endNode().setNodeCode("end").add()
                .build();

        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        // 【启动引擎】
        engine.start();
        assert engine.isStarted();

        // 【提交】
        TestActionParameter parameter = new TestActionParameter();
        parameter.setOperateTime(new Date());

        parameter.setData(Integer.valueOf(500));
        engine.action("begin", "submit", parameter);
        assert definition.getCurrentNodeCode().equals("并行节点");

        // 【并行节点 - 复核】
        parameter.setOperator("复核人");
        engine.action("复核", "通过", parameter);
        assert definition.getCurrentNodeCode().equals("并行节点");

        // 【并行节点 - 复核】
        parameter.setOperator("预算计划人");
        engine.action("预算", "完成", parameter);
        assert definition.getCurrentNodeCode().equals("end");
        assert engine.isFinished();
    }

    //#endregion

    //#region 退回流程

    @Test
    @DisplayName("测试退回流程")
    public void test_flowBack() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
            .setFlowCode("退回流程")
            .setCancelable(true)
            .beginNode().setNodeCode("开始")
                .state().setStateCode("Pass").setToNodeCode("普通节点").add()
                .add()
            .node().setNodeCode("普通节点")
                .state().setStateCode("Pass").setToNodeCode("复合节点").add()
                .add()
            .compositeNode(node -> node.resolveAllState("Pass")).setNodeCode("复合节点")
                .partial().setItemCode("Part1").add()
                .partial().setItemCode("Part2").add()
                .state().setStateCode("Pass").setToNodeCode("分支节点").add()
                .add()
            .forkNode().setNodeCode("分支节点")
                .state((d, n) -> true).setToNodeCode("并行节点").add()
                .add()
            .parallelNode().setNodeCode("并行节点")
                .partialNode().setItemCode("P1")
                    .addState("Pass", "通过")
                    .add()
                .partialNode().setItemCode("P2")
                    .addState("Pass", "通过")
                    .add()
                .state((d, n) -> true).setToNodeCode("结束").add()
                .add()
            .endNode().setNodeCode("结束")
                .add()
            .build();

        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);
        assert engine.getStatus() == FlowStatus.Pending;

        engine.start();
        assert engine.isStarted();
        assert definition.getCurrentNodeCode().equals("开始");

        TestActionParameter parameter = null;

        // 【普通节点 > 开始】
        System.out.println("\n【普通节点 > 开始】");
        // 1. 设置到普通节点
        engine.action("开始", "Pass");
        assert definition.getCurrentNodeCode().equals("普通节点");
        // 2. 从【普通节点】退回到【开始节点】
        parameter = new TestActionParameter();
        parameter.setOperateTime(new Date());
        parameter.setOperator("普通节点审核员");
        parameter.setOperatorName("王军");
        StateMachineFlowState state = engine.back("普通节点", parameter);
        assert state.getToNodeCode().equals(definition.getCurrentNodeCode());
        assert definition.getCurrentNodeCode().equals("开始");

        // 【复合节点 > 普通节点】
        Action1<String> compositionBackAction = (itemCode) -> {
            TestActionParameter param = null;
            resetPartialItems(definition, "复合节点");
            System.out.println("\n【复合节点 > 普通节点】");
            // 1. 设置到复合节点
            definition.setCurrentNodeCode("普通节点");
            definition.setPreviousNodeCode("开始节点");
            definition.setPreviousStateCode("Pass");
            engine.action("普通节点", "Pass");
            assert definition.getCurrentNodeCode().equals("复合节点");

            StateMachineFlowState backState = null;
            for(int i = 1; i <= 2; i++) {
                String partialCode = "Part" + i;
                param = new TestActionParameter();
                param.setOperateTime(new Date());
                param.setOperator(partialCode + "审核员");
                param.setItemCode(partialCode);
                if(partialCode.equals(itemCode)) {
                    backState = engine.back("复合节点", param);
                    break;
                } else {
                    engine.action("复合节点", "Pass", param);
                }
            }
            if(backState != null) {
                CompositionPartialState partialState = (CompositionPartialState) backState.getPreviousFlowState();
                Assert.assertNotNull(partialState);
                Assert.assertTrue(partialState.getActionPartialItem().getItemCode().equals(itemCode));
                if(itemCode.equals("Part1")) {
                    Assert.assertTrue(partialState.getRelationPartialItems().size() == 1);
                } else {
                    Assert.assertTrue(partialState.getRelationPartialItems().isEmpty());
                }
            }
            assert definition.getCurrentNodeCode().equals("普通节点");
        };
        // Part1 退回
        compositionBackAction.apply("Part1");
        // Part2 退回
        compositionBackAction.apply("Part2");

        // 【并行节点 > 复合节点】
        Action1<String> parallelBackAction = (parallelNodeCode) -> {
            TestActionParameter param = null;

            System.out.println("\n【并行节点 > 复合节点】");
            // 1. 设置到复合节点
            definition.setCurrentNodeCode("复合节点");
            definition.setPreviousNodeCode("普通节点");
            definition.setPreviousStateCode("Pass");
            
            resetPartialItems(definition, "复合节点");

            param = new TestActionParameter();
            param.setItemCode("Part1");
            engine.action("复合节点", "Pass", param);
            param.setItemCode("Part2");
            engine.action("复合节点", "Pass", param);
            assert definition.getCurrentNodeCode().equals("并行节点");

            resetPartialItems(definition, "并行节点");

            // 2. 节点退回
            StateMachineFlowState backState = null;
            for(int i = 1; i <= 2; i++) {
                String itemCode = "P" + i;
                param = new TestActionParameter();
                param.setOperateTime(new Date());
                param.setOperator(itemCode + "审核员");
                if(itemCode.equals(parallelNodeCode)) {
                    backState = engine.back(itemCode, param);
                    break;
                } else {
                    engine.action(itemCode, "Pass", param);
                }
            }

            if(backState != null) {
                ParallelActionNodeState parallelActionState = (ParallelActionNodeState) backState.getPreviousFlowState();
                Assert.assertNotNull(parallelActionState);
                Assert.assertTrue(parallelActionState.getActionPartialItem().getItemCode().equals(parallelNodeCode));
                if(parallelNodeCode.equals("P1")) {
                    Assert.assertTrue(parallelActionState.getRelationPartialItems().size() == 1);
                } else {
                    Assert.assertTrue(parallelActionState.getRelationPartialItems().isEmpty());
                }
            }
            assert definition.getCurrentNodeCode().equals("复合节点");
        };
        // P1 退回
        parallelBackAction.apply("P1");
        // P2 退回
        parallelBackAction.apply("P2");
    }

    //#endregion

    //#region 取消流程

    @Test
    @DisplayName("测试流程取消")
    public void test_flowCancel() {
        StateMachineFlowDefinition definition = 
            factory.definitionBuilder()
                .setFlowCode("完全类型流程")
                .setCancelable(true)
                .beginNode().setNodeCode("开始")
                    .state().setStateCode("Next").setToNodeCode("复合").add()
                    .add()
                .compositeNode(node -> node.resolveAllState("Next")).setNodeCode("复合")
                    .partial().setItemCode("Part1").add()
                    .partial().setItemCode("Part2").add()
                    .state().setStateCode("Next").setToNodeCode("分支").add()
                .add()
                .forkNode().setNodeCode("分支")
                    .state((data, forkNode) -> true).setToNodeCode("并行").add()
                .add()
                .parallelNode().setNodeCode("并行")
                    .partialNode()
                        .setItemCode("并行1")
                        .addState("Done", null)
                    .add()
                    .partialNode()
                        .setItemCode("并行2")
                        .addState("Done", null)
                    .add()
                .add()
                .endNode().setNodeCode("结束").add()
                .build();
        StateMachineFLowEngine<StateMachineFlowDataQuery> engine = factory.createEngine(definition);

        assert engine.getStatus() == FlowStatus.Pending;

        FlowActionParameter parameter = null;

        // 普通节点取消
        engine.start();
        assert engine.isStarted();

        parameter = new FlowActionParameter();
        parameter.setOperator("Starter");
        engine.cancel("开始", parameter);
        assert engine.isCanceled();
        assert definition.getCurrentNodeCode() == null && definition.getPreviousNodeCode().equals("开始") && definition.getPreviousStateCode().equals("@Cancel");

        // 复合节点取消
        definition.setStatus(FlowStatus.Started);
        definition.setCurrentNodeCode("复合");
        definition.setPreviousNodeCode("开始");
        definition.setPreviousStateCode("Next");

        parameter = new FlowActionParameter();
        parameter.setOperator("Part1");
        engine.cancel("复合", parameter);
        assert engine.isCanceled();
        assert definition.getCurrentNodeCode() == null && definition.getPreviousNodeCode().equals("复合") && definition.getPreviousStateCode().equals("@Cancel");
        ((StateMachineCompositeNode)definition.findNode("复合")).forEach((i, idx, b) -> {
            if(idx > 0) {
                Assert.assertTrue(i.getStatus() == StateMachinePartialItemStatus.Terminated);
            }
        });

        // 并行节点取消
        definition.setStatus(FlowStatus.Started);
        definition.setCurrentNodeCode("并行");
        definition.setPreviousNodeCode("复合");
        definition.setPreviousStateCode("Next");

        parameter = new FlowActionParameter();
        parameter.setOperator("操作人");
        engine.action("并行1", "Done", parameter);
        engine.cancel("并行2", parameter);
        assert engine.isCanceled();
        assert definition.getCurrentNodeCode() == null && definition.getPreviousNodeCode().equals("并行") && definition.getPreviousStateCode().equals("@Cancel");
        ((StateMachineParallelNode)definition.findNode("并行")).forEach((i, idx, b) -> {
            if(i.getItemCode().equals("并行1")) {
                Assert.assertTrue(i.getStateCode().equals("Done"));
            }
            if(i.getItemCode().equals("并行2")) {
                Assert.assertTrue(i.getStateCode().equals("@Cancel"));
            }
        });
    }

    //#endregion

    //#region helper methonds

    private StateMachineFlowFactory<StateMachineFlowDataQuery> createFactory() {
        StateMachineFlowRepository repository = new StateMachineFlowRepository();
        repository.setDefinitionFn(p -> createDefaultTestDefinition());

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
        StateMachineFlowNode node = definition.findNode(nodeCode);
        List<StateMachinePartialItem> itemList;
        if(node instanceof StateMachineCompositeNode compositeNode) {
            itemList = compositeNode.getPartialItemList();
        } else if(node instanceof StateMachineParallelNode parallelNode) {
            itemList = parallelNode.getPartialItemList();
        } else {
            return;
        }
        if(itemList != null) {
            itemList.forEach(i -> {
                i.setStateCode(null);
                i.setStatus(StateMachinePartialItemStatus.Pending);
            });
        }
    }

    public static class TestActionParameter 
            extends FlowActionParameter 
            implements IPartialItemCode, IFlowDataGetter {
        
        private String itemCode;
        private Object data;

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        @Override
        public String getItemCode() {
            return itemCode;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public Object getData() {
            return data;
        }
    }

    //#endregion
    
}
