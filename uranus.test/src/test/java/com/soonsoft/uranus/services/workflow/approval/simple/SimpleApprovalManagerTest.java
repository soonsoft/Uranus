package com.soonsoft.uranus.services.workflow.approval.simple;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.services.approval.ApprovalManagerFactory;
import com.soonsoft.uranus.services.approval.IApprovalManager;
import com.soonsoft.uranus.services.approval.IApprovalRepository;
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
        IApprovalManager<SimpleApprovalQuery> manager = createApprovalManager();
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

        String beginNodeCode = manager.getFlowDefinition(record.getApprovalType()).getBeginNode().getNodeCode();
        String endNodeCode = manager.getFlowDefinition(record.getApprovalType()).getEndNode().getNodeCode();
        Assert.assertNotNull(record);
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Checking);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【KYC 审核通过】
        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "香港KYC审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规审核"));

        // 【合规 审核驳回】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "新加坡合规审核人");
        record = manager.deny(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 再次提交】
        ApprovalParameter resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        setOperator(resubmitParam, "制单人");
        record = manager.resubmit(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【KYC 审核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "新加坡KYC审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("合规审核"));

        // 【合规 审核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "香港合规审核人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("RO复核"));

        // 【RO 复核通过】
        checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "国际RO负责人");
        record = manager.approve(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(endNodeCode));
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Completed);

    }

    // 撤回与取消测试
    @Test
    public void test_revokeAndCancel() {
        IApprovalManager<SimpleApprovalQuery> manager = createApprovalManager();
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

        String beginNodeCode = manager.getFlowDefinition(record.getApprovalType()).getBeginNode().getNodeCode();
        Assert.assertNotNull(record);
        Assert.assertTrue(record.getStatus() == ApprovalStatus.Checking);
        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        //【制单人 撤回】
        ApprovalParameter resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        setOperator(resubmitParam, "制单人");
        record = manager.revoke(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 再次提交】
        resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        setOperator(resubmitParam, "制单人");
        record = manager.resubmit(resubmitParam);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals("KYC审核"));

        // 【复核 通过】
        ApprovalCheckParameter checkParameter = new ApprovalCheckParameter();
        checkParameter.setRecordCode(record.getRecordCode());
        setOperator(checkParameter, "复核人");
        record = manager.deny(checkParameter);

        Assert.assertTrue(record.getFlowState().getToNodeCode().equals(beginNodeCode));

        // 【制单人 取消 】
        resubmitParam = new ApprovalParameter();
        resubmitParam.setRecordCode(record.getRecordCode());
        setOperator(resubmitParam, "制单人");
        manager.cancel(resubmitParam);

        Assert.assertTrue(record.getStatus() == ApprovalStatus.Canceled);
    }

    // 测试会签
    @Test
    public void test_countersign() {

    }

    private IApprovalManager<SimpleApprovalQuery> createApprovalManager() {
        ApprovalManagerFactory<SimpleApprovalQuery> factory = new ApprovalManagerFactory<>();
        SimpleApprovalQuery query = new SimpleApprovalQuery();
        IApprovalRepository repository = new ApprovalRepositoryImpl();
        IApprovalManager<SimpleApprovalQuery> manager = 
            factory.createManager(
                query, repository, 
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
                        .end()
            );
        return manager;
    }

    private void setOperator(FlowActionParameter p, String name) {
        p.setOperateTime(new Date());
        p.setOperator(name);
        p.setOperatorName(name);
    }
    
}
