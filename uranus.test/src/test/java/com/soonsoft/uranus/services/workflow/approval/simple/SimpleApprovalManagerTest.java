package com.soonsoft.uranus.services.workflow.approval.simple;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.struct.tuple.Tuple2;
import com.soonsoft.uranus.services.approval.ApprovalManagerFactory;
import com.soonsoft.uranus.services.approval.IApprovalManager;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
import com.soonsoft.uranus.services.approval.ApprovalManagerFactory.ApprovalDefinitionContainer;
import com.soonsoft.uranus.services.approval.ApprovalManagerFactory.ApprovalNodeType;
import com.soonsoft.uranus.services.approval.model.ApprovalCheckParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalCreateParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalData;
import com.soonsoft.uranus.services.approval.model.ApprovalParameter;
import com.soonsoft.uranus.services.approval.model.ApprovalRecord;
import com.soonsoft.uranus.services.approval.model.ApprovalStatus;
import com.soonsoft.uranus.services.workflow.approval.ApprovalRepositoryImpl;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class SimpleApprovalManagerTest {

    // 正向流程
    @Test
    public void test_normalApprove() {
        Tuple2<IApprovalManager<SimpleApprovalQuery>, ApprovalDefinitionContainer>  tuple2 = createApprovalManager();
        IApprovalManager<SimpleApprovalQuery> manager = tuple2.getItem1();
        ApprovalDefinitionContainer container = tuple2.getItem2();
        String approvalType = "开户审核";

        // 【提交审核】
        ApprovalCreateParameter submitParam = new ApprovalCreateParameter();
        submitParam.setSource("LP");
        submitParam.setApprovalType(approvalType);
        submitParam.setBusinessCode("Account");
        submitParam.setEntityCode("LP0032");
        setOperator(submitParam, "制单人");
        ApprovalData approvalData = new ApprovalData();
        approvalData.setTargetId(1);
        approvalData.setTargetType("AccountIdentity");
        submitParam.addApprovalData(approvalData);
        ApprovalRecord record = manager.submit(submitParam);

        String beginNodeCode = container.getBeginNodeCode(approvalType);
        String endNodeCode = container.getEndNodeCode(approvalType);
        Assert.assertNotNull(record);
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Checking);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【KYC 审核通过】
        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("KYC审核");
        setOperator(checkParameter, "香港KYC审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规审核"));

        // 【合规 审核驳回】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("合规审核");
        setOperator(checkParameter, "新加坡合规审核人");
        record = manager.deny(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 再次提交】
        ApprovalParameter resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        resubmitParam.setActionNodeCode(beginNodeCode);
        setOperator(resubmitParam, "制单人");
        record = manager.resubmit(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【KYC 审核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("KYC审核");
        setOperator(checkParameter, "新加坡KYC审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规审核"));

        // 【合规 审核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("合规审核");
        setOperator(checkParameter, "香港合规审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("RO复核"));

        // 【RO 复核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("RO复核");
        setOperator(checkParameter, "国际RO负责人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(endNodeCode));
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Completed);

    }

    // 撤回与取消测试
    @Test
    public void test_revokeAndCancel() {
        Tuple2<IApprovalManager<SimpleApprovalQuery>, ApprovalDefinitionContainer>  tuple2 = createApprovalManager();
        IApprovalManager<SimpleApprovalQuery> manager = tuple2.getItem1();
        ApprovalDefinitionContainer container = tuple2.getItem2();
        String approvalType = "开户审核";

        // 【提交审核】
        ApprovalCreateParameter submitParam = new ApprovalCreateParameter();
        submitParam.setSource("GP");
        submitParam.setApprovalType(approvalType);
        submitParam.setBusinessCode("Account");
        submitParam.setEntityCode("LP0033");
        setOperator(submitParam, "制单人");
        ApprovalData approvalData = new ApprovalData();
        approvalData.setTargetId(1);
        approvalData.setTargetType("AccountIdentity");
        submitParam.addApprovalData(approvalData);
        ApprovalRecord record = manager.submit(submitParam);

        String beginNodeCode = container.getBeginNodeCode(approvalType);
        Assert.assertNotNull(record);
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Checking);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        //【制单人 撤回】
        ApprovalParameter resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        resubmitParam.setActionNodeCode("KYC审核");
        setOperator(resubmitParam, "制单人");
        record = manager.revoke(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 再次提交】
        resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        resubmitParam.setActionNodeCode(beginNodeCode);
        setOperator(resubmitParam, "制单人");
        record = manager.resubmit(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【合规审核 通过】
        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode("KYC审核");
        setOperator(checkParameter, "香港合规");
        record = manager.deny(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 取消 】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        checkParameter.setActionNodeCode(beginNodeCode);
        setOperator(checkParameter, "制单人");
        manager.cancel(checkParameter);

        Assert.assertTrue(record.getStatus() == ApprovalStatus.Canceled);
    }

    // 测试会签
    @Test
    public void test_countersign() {
        Tuple2<IApprovalManager<SimpleApprovalQuery>, ApprovalDefinitionContainer>  tuple2 = createApprovalManager();
        IApprovalManager<SimpleApprovalQuery> manager = tuple2.getItem1();
        ApprovalDefinitionContainer container = tuple2.getItem2();
        String approvalType = "信息变更";
        String endNodeCode = container.getEndNodeCode(approvalType);

        // 【提交审核】
        ApprovalCreateParameter submitParam = new ApprovalCreateParameter();
        submitParam.setSource("GP");
        submitParam.setApprovalType(approvalType);
        submitParam.setBusinessCode("Account");
        submitParam.setEntityCode("LP0032");
        setOperator(submitParam, "信息变更提交人");
        ApprovalData approvalData = new ApprovalData();
        approvalData.setTargetId(1);
        approvalData.setTargetType("AccountIdentity");
        submitParam.addApprovalData(approvalData);
        ApprovalRecord record = manager.submit(submitParam);

        Assert.assertNotNull(record);
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Checking);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("复核"));

         // 【复核 通过】
         ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
         checkParameter.setRecordCode(record.getRecordCode());
         checkParameter.setActionNodeCode("复核");
         setOperator(checkParameter, "复核人");
         record = manager.approve(checkParameter);
 
         Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规"));

         // 【合规 Part1 法务 通过】
         checkParameter = new ApprovalCheckParameter();
         checkParameter.setRecordCode(record.getRecordCode());
         checkParameter.setActionNodeCode("合规");
         checkParameter.setItemCode("法务审核");
         setOperator(checkParameter, "法务合规人");
         record = manager.approve(checkParameter);
 
         Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规"));
         
         // 【合规 Part2 RO审核 通过】
         checkParameter = new ApprovalCheckParameter();
         checkParameter.setRecordCode(record.getRecordCode());
         checkParameter.setActionNodeCode("合规");
         checkParameter.setItemCode("RO审核");
         setOperator(checkParameter, "新加坡RO");
         record = manager.approve(checkParameter);
 
         Assert.assertTrue(record.getFlowState().getToNodeCode().equals(endNodeCode));
         Assert.assertTrue(record.getStatus() == ApprovalStatus.Completed);
    }

    private Tuple2<IApprovalManager<SimpleApprovalQuery>, ApprovalDefinitionContainer> createApprovalManager() {
        ApprovalManagerFactory<SimpleApprovalQuery> factory = new ApprovalManagerFactory<>();
        SimpleApprovalQuery query = new SimpleApprovalQuery();
        IApprovalRepository repository = new ApprovalRepositoryImpl();
        ApprovalDefinitionContainer container = 
            factory.definitionContainer()
                .definition("开户审核", "开户", true)
                    .begin()
                    .next("KYC审核", "KYC审核")
                    .next("合规审核", "合规审核")
                    .next("RO复核", "RO复核")
                    .end()
                .definition("信息变更", "KYC信息更新", false)
                    .begin()
                    .next("复核", "信息变更复核")
                    .next(
                        "合规", 
                        "香港合规审核", 
                        ApprovalNodeType.All, 
                        node -> 
                            node.partial()
                                    .setItemCode("法务审核")
                                    .setItemName("法务审核")
                                    .add()
                                .partial()
                                    .setItemCode("RO审核")
                                    .setItemName("RO审核")
                                    .add()
                    )
                    .end();
        IApprovalManager<SimpleApprovalQuery> manager = factory.createManager( query, repository, container);
        return new Tuple2<>(manager, container);
    }

    private void setOperator(FlowActionParameter p, String name) {
        p.setOperateTime(new Date());
        p.setOperator(name);
        p.setOperatorName(name);
    }
    
}
